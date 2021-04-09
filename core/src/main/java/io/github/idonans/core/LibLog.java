package io.github.idonans.core;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Lib 内的统一日志输出
 *
 * @author idonans
 * @version 1.1
 */
public class LibLog {

    /**
     * 日志打印级别, 默认 {@linkplain Log#ERROR}
     */
    private static int LOG_LEVEL = Log.ERROR;
    private static final String LOG_TAG;

    static {
        final String logTag = BuildConfig.LIB_NAME + "_" + BuildConfig.LIB_VERSION_NAME + "(" + BuildConfig.LIB_VERSION_CODE + ")";
        final int length = logTag.length();
        if (length > 23) {
            LOG_TAG = logTag.substring(0, 23);
        } else {
            LOG_TAG = logTag;
        }
    }

    /**
     * 设置日志级别，可取值为 {@linkplain Log#ERROR}, {@linkplain Log#WARN}, {@linkplain Log#INFO},
     * {@linkplain Log#DEBUG}, {@linkplain Log#VERBOSE}
     */
    public static void setLogLevel(int logLevel) {
        LOG_LEVEL = logLevel;
    }

    /**
     * 获取当前日志级别
     */
    public static int getLogLevel() {
        return LOG_LEVEL;
    }

    private static boolean isLoggable(int logLevel) {
        return logLevel >= LOG_LEVEL;
    }

    public static void v(@Nullable String msg, Object... args) {
        v(null, msg, args);
    }

    public static void v(@NonNull Throwable e) {
        v(e, null);
    }

    public static void v(@Nullable Throwable e, @Nullable String msg, Object... args) {
        if (!isLoggable(Log.VERBOSE)) {
            return;
        }
        if (e != null) {
            Log.v(LOG_TAG, formatMessage(msg, args), e);
            return;
        }

        Log.v(LOG_TAG, formatMessage(msg, args));
    }

    public static void d(@Nullable String msg, Object... args) {
        d(null, msg, args);
    }

    public static void d(@NonNull Throwable e) {
        d(e, null);
    }

    public static void d(@Nullable Throwable e, @Nullable String msg, Object... args) {
        if (!isLoggable(Log.DEBUG)) {
            return;
        }
        if (e != null) {
            Log.d(LOG_TAG, formatMessage(msg, args), e);
            return;
        }

        Log.d(LOG_TAG, formatMessage(msg, args));
    }

    public static void i(@Nullable String msg, Object... args) {
        i(null, msg, args);
    }

    public static void i(@NonNull Throwable e) {
        i(e, null);
    }

    public static void i(@Nullable Throwable e, @Nullable String msg, Object... args) {
        if (!isLoggable(Log.INFO)) {
            return;
        }
        if (e != null) {
            Log.i(LOG_TAG, formatMessage(msg, args), e);
            return;
        }

        Log.i(LOG_TAG, formatMessage(msg, args));
    }

    public static void w(@Nullable String msg, Object... args) {
        w(null, msg, args);
    }

    public static void w(@NonNull Throwable e) {
        w(e, null);
    }

    public static void w(@Nullable Throwable e, @Nullable String msg, Object... args) {
        if (!isLoggable(Log.WARN)) {
            return;
        }
        if (e != null) {
            Log.w(LOG_TAG, formatMessage(msg, args), e);
            return;
        }

        Log.w(LOG_TAG, formatMessage(msg, args));
    }

    public static void e(@Nullable String msg, Object... args) {
        e(null, msg, args);
    }

    public static void e(@NonNull Throwable e) {
        e(e, null);
    }

    public static void e(@Nullable Throwable e, @Nullable String msg, Object... args) {
        if (!isLoggable(Log.ERROR)) {
            return;
        }
        if (e != null) {
            Log.e(LOG_TAG, formatMessage(msg, args), e);
            return;
        }

        Log.e(LOG_TAG, formatMessage(msg, args));
    }

    @NonNull
    private static String formatMessage(@Nullable String msg, Object... args) {
        if (msg == null) {
            return "null";
        }
        if (args != null) {
            return String.format(msg, args);
        }
        return msg;
    }

}
