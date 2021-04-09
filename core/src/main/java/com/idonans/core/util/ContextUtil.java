package com.idonans.core.util;

import android.content.Context;

import androidx.annotation.NonNull;

import io.github.idonans.appcontext.AppContext;

public class ContextUtil {

    private ContextUtil() {
    }

    @NonNull
    public static Context getContext() {
        return AppContext.getContext();
    }

}
