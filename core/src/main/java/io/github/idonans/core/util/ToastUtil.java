package io.github.idonans.core.util;

import android.widget.Toast;

import io.github.idonans.core.thread.Threads;

public class ToastUtil {

    private ToastUtil() {
    }

    public static void show(String msg) {
        Threads.postUi(() -> Toast.makeText(ContextUtil.getContext(), String.valueOf(msg), Toast.LENGTH_SHORT).show());
    }

    public static void showLong(String msg) {
        Threads.postUi(() -> Toast.makeText(ContextUtil.getContext(), String.valueOf(msg), Toast.LENGTH_LONG).show());
    }

}
