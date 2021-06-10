package io.github.idonans.core.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 共享的线程池
 */
public class ThreadPool {

    private static class InstanceHolder {

        private static final ThreadPool sInstance = new ThreadPool();
    }

    public static ThreadPool getInstance() {
        return InstanceHolder.sInstance;
    }

    private static final String THREAD_NAME = "thread_pool";

    private final AtomicInteger mCount = new AtomicInteger();
    private final Executor mExecutor = Executors.newCachedThreadPool();

    private ThreadPool() {
    }

    public void post(final Runnable runnable) {
        mExecutor.execute(() -> {
            mCount.incrementAndGet();
            try {
                Thread.currentThread().setName(THREAD_NAME);
                android.os.Process.setThreadPriority(
                        android.os.Process.THREAD_PRIORITY_BACKGROUND);
                runnable.run();
            } finally {
                mCount.decrementAndGet();
            }
        });
    }

    /**
     * 获得线程池中正在运行的任务数量 (任务数量可能比当前线程池中的线程数量少)
     */
    public int getCount() {
        return mCount.get();
    }
}
