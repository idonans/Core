package io.github.idonans.core;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class WeakAbortSignal extends SimpleAbortSignal {

    private final WeakReference<Object> mWeakReference;

    public WeakAbortSignal(@Nullable Object object) {
        mWeakReference = new WeakReference<>(object);
    }

    public Object getObject() {
        return mWeakReference.get();
    }

    @Override
    public boolean isAbort() {
        if (super.isAbort()) {
            return true;
        }

        Object object = mWeakReference.get();
        if (object == null) {
            setAbort();
            return true;
        }

        if (object instanceof AbortSignal) {
            AbortSignal abortSignal = (AbortSignal) object;
            if (abortSignal.isAbort()) {
                setAbort();
                return true;
            }
        }

        return false;
    }

}
