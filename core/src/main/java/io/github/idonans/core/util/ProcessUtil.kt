package io.github.idonans.core.util

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import androidx.annotation.RequiresApi

/**
 * author:zuoweichen
 * DAte:2023-11-01 16:05
 * Description:描述
 */
object ProcessUtil {
    private var currentProcessName: String? = null

    /**
     * 获取当前进程名
     * @param context Context
     * @return String?
     */
    @JvmStatic
    fun getCurrentProcessName(context: Context): String? {
        if (!currentProcessName.isNullOrEmpty()) {
            return currentProcessName!!
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            currentProcessName = getCurrentProcessNameByApplication()
            return currentProcessName!!
        }
        currentProcessName = getCurrentProcessNameByActivityThread()
        if (!currentProcessName.isNullOrEmpty()) {
            return currentProcessName!!
        }
        currentProcessName = getCurrentProcessNameByActivityManager(context)
        return currentProcessName
    }

    /**
     * 通过Application新的API获取进程名
     * @return String?
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.P)
    fun getCurrentProcessNameByApplication(): String? {
        return Application.getProcessName()
    }

    /**
     * 通过反射ActivityThread获取进程名
     */
    @JvmStatic
    fun getCurrentProcessNameByActivityThread(): String? {
        var processName: String? = null
        try {
            val declaredMethod =
                Class.forName(
                    "android.app.ActivityThread",
                    false,
                    Application::class.java.classLoader
                )
                    .getDeclaredMethod(
                        "currentProcessName",
                        *arrayOfNulls<Class<*>?>(0)
                    )
            declaredMethod.isAccessible = true
            val invoke = declaredMethod.invoke(null, arrayOfNulls<Any>(0))
            if (invoke is String) {
                processName = invoke
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return processName
    }

    /**
     * 通过ActivityManager 获取进程名
     * @param context Context
     * @return String?
     */
    @JvmStatic
    fun getCurrentProcessNameByActivityManager(context: Context): String? {
        val pid = Process.myPid()
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        if (am != null) {
            val runningAppList = am.runningAppProcesses
            if (!runningAppList.isNullOrEmpty()) {
                for (processInfo in runningAppList) {
                    if (processInfo.pid == pid) {
                        return processInfo.processName
                    }
                }
            }
        }
        return null
    }
}