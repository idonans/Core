package com.idonans.lang.util;

import android.content.Context;

import androidx.annotation.NonNull;

import com.idonans.appcontext.AppContext;

public class ContextUtil {

    private ContextUtil() {
    }

    @NonNull
    public static Context getContext() {
        return AppContext.getContext();
    }

}
