package com.idonans.lang.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextUtil {

    private TextUtil() {
    }

    @Nullable
    public static String trim(String string) {
        if (string != null) {
            string = string.trim();
        }
        return string;
    }

    @NonNull
    public static <T extends CharSequence> T checkStringNotEmpty(final T string) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException();
        }
        return string;
    }

    @NonNull
    public static <T extends CharSequence> T checkStringNotEmpty(final T string, final Object errorMessage) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return string;
    }

}
