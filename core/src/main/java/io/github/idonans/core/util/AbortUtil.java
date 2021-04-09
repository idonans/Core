package io.github.idonans.core.util;

import androidx.annotation.Nullable;

import io.github.idonans.core.AbortException;
import io.github.idonans.core.AbortSignal;

public class AbortUtil {

    /**
     * @param abortSignal
     * @return true if abortSignal is not null and isAbort return true, otherwise false.
     */
    public static boolean isAbort(@Nullable AbortSignal abortSignal) {
        if (abortSignal == null) {
            return false;
        }
        return abortSignal.isAbort();
    }

    /**
     * @see #isAbort(AbortSignal)
     */
    public static void throwIfAbort(@Nullable AbortSignal abortSignal) {
        if (isAbort(abortSignal)) {
            throw new AbortException();
        }
    }

}
