package io.github.idonans.core.thread;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import io.github.idonans.core.CoreLog;
import io.github.idonans.core.util.Preconditions;

public class BatchQueue<T> {

    private final TaskQueue mDispatchQueue = new TaskQueue(1);
    private final boolean mPostToUiThread;

    @NonNull
    private final ReentrantLock mLock = new ReentrantLock();

    @NonNull
    private List<T> mPayloadList = new ArrayList<>();

    @Nullable
    private MergeFunction<T> mMergeFunction;

    @Nullable
    private Consumer<List<T>> mConsumer;

    public BatchQueue() {
        this(false);
    }

    public BatchQueue(boolean postToUiThread) {
        mPostToUiThread = postToUiThread;
    }

    public void setMergeFunction(@Nullable MergeFunction<T> mergeFunction) {
        mMergeFunction = mergeFunction;
    }

    public void setConsumer(@Nullable Consumer<List<T>> consumer) {
        mConsumer = consumer;
        dispatch();
    }

    public void add(@Nullable T payload) {
        mLock.lock();
        try {
            MergeFunction<T> mergeFunction = mMergeFunction;
            if (mergeFunction == null) {
                mergeFunction = mDefaultMergeFunction;
            }
            Preconditions.checkNotNull(mPayloadList);
            mPayloadList = mergeFunction.merge(mPayloadList, payload);
            Preconditions.checkNotNull(mPayloadList);
        } finally {
            mLock.unlock();
        }

        dispatch();
    }

    private void dispatch() {
        mDispatchQueue.skipQueue();
        mDispatchQueue.enqueue(() -> {
            if (mPostToUiThread) {
                Threads.postUi(BatchQueue.this::onDispatch);
            } else {
                onDispatch();
            }
        });
    }

    private final MergeFunction<T> mDefaultMergeFunction = (payloadList, payload) -> {
        payloadList.add(payload);
        return payloadList;
    };

    public interface MergeFunction<T> {
        @NonNull
        List<T> merge(@NonNull List<T> payloadList, @Nullable T payload);
    }

    private void onDispatch() {
        final Consumer<List<T>> consumer = mConsumer;
        if (consumer == null) {
            CoreLog.v("consumer is null");
            return;
        }
        final List<T> payloadList;
        mLock.lock();
        try {
            payloadList = mPayloadList;
            mPayloadList = new ArrayList<>();
        } finally {
            mLock.unlock();
        }
        if (payloadList.isEmpty()) {
            return;
        }
        consumer.accept(payloadList);
        dispatch();
    }

}
