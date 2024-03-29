package io.github.idonans.core.thread;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * 任务队列，所有任务都在共享的线程池中执行
 */
public class TaskQueue {

    private final Object mLock = new Object();
    /**
     * 同时执行的最大任务数量
     */
    private int mMaxCount;
    /**
     * 当前正在执行的任务数量
     */
    private int mCurrentCount;
    /**
     * 当前正在等待的任务数量
     */
    private int mWaitCount;

    private final Deque<Task> mQueue = new LinkedList<>();
    private final List<Task> mRunningTask = new ArrayList<>();

    private Object mRecheckQueueTag;

    public TaskQueue(int maxCount) {
        if (maxCount <= 0) {
            throw new IllegalArgumentException("max count must > 0, max count:" + maxCount);
        }
        this.mMaxCount = maxCount;
    }

    public int getWaitCount() {
        return this.mWaitCount;
    }

    public void setMaxCount(int maxCount) {
        if (maxCount <= 0) {
            return;
        }
        synchronized (mLock) {
            if (this.mMaxCount == maxCount) {
                return;
            }
            this.mMaxCount = maxCount;
        }
        recheckQueue();
    }

    public int getMaxCount() {
        return this.mMaxCount;
    }

    public int getCurrentCount() {
        return this.mCurrentCount;
    }

    public int getRunningTaskSize() {
        return this.mRunningTask.size();
    }

    public void skipQueue() {
        synchronized (mLock) {
            for (Task item : mQueue) {
                item.setSkip();
            }
        }
    }

    public void enqueue(Runnable runnable) {
        enqueue(runnable, false);
    }

    public void enqueue(Runnable runnable, boolean first) {
        final Task task = new Task(runnable) {
            @Override
            public void run() {
                super.run();
                synchronized (mLock) {
                    mCurrentCount--;
                    if (!mRunningTask.remove(this)) {
                        throw new IllegalStateException("fail to remove task " + this);
                    }
                }
                recheckQueue();
            }
        };
        final boolean addToQueue;
        synchronized (this.mLock) {
            if (this.mCurrentCount < this.mMaxCount) {
                this.mCurrentCount++;
                mRunningTask.add(task);
                addToQueue = false;
            } else {
                this.mWaitCount++;
                if (first) {
                    this.mQueue.addFirst(task);
                } else {
                    this.mQueue.addLast(task);
                }
                addToQueue = true;
            }
        }
        if (!addToQueue) {
            ThreadPool.getInstance().post(task);
        }
    }

    private void recheckQueue() {
        final Object recheckQueueTag = new Object();
        mRecheckQueueTag = recheckQueueTag;
        ThreadPool.getInstance().post(() -> {
            for (; mRecheckQueueTag == recheckQueueTag && recheckQueueInternal(); ) ;
        });
    }

    private boolean recheckQueueInternal() {
        Task postToRun;
        synchronized (this.mLock) {
            if (this.mCurrentCount < this.mMaxCount) {
                postToRun = this.mQueue.pollFirst();
                if (postToRun != null) {
                    this.mWaitCount--;
                    this.mCurrentCount++;
                    mRunningTask.add(postToRun);
                }
            } else {
                postToRun = null;
            }
        }
        if (postToRun != null) {
            ThreadPool.getInstance().post(postToRun);
        }
        return postToRun != null;
    }

    public void printDetail(StringBuilder builder) {
        String tag = "TaskQueue";
        builder.append("--").append(tag).append("--\n");
        builder.append("--max count:").append(getMaxCount()).append("--\n");
        builder.append("--current count:").append(getCurrentCount()).append("--\n");
        builder.append("--running task size:").append(getRunningTaskSize()).append("--\n");
        builder.append("--wait count:").append(getWaitCount()).append("--\n");
        builder.append("--").append(tag).append("--end\n");
    }

    private static class Task implements Runnable {

        private final Runnable mTarget;
        private boolean mSkip;

        public Task(Runnable target) {
            this.mTarget = target;
        }

        private void setSkip() {
            this.mSkip = true;
        }

        @Override
        public void run() {
            if (!mSkip) {
                this.mTarget.run();
            }
        }
    }

}
