package io.github.idonans.core.manager;

import io.github.idonans.core.Constants;
import io.github.idonans.core.CoreLog;
import io.github.idonans.core.Singleton;

import java.util.UUID;

/**
 * 不同进程的值不同. 进程重启之后值不会变更。但是如果应用程序卸载了或者清除了数据，值会变更。
 */
public class AppIDManager {

    private static final Singleton<AppIDManager> INSTANCE =
            new Singleton<AppIDManager>() {
                @Override
                protected AppIDManager create() {
                    return new AppIDManager();
                }
            };

    public static AppIDManager getInstance() {
        return INSTANCE.get();
    }

    private static final String KEY_APP_ID = Constants.GLOBAL_PREFIX + "app_id";

    private final String mAppID;

    private AppIDManager() {
        CoreLog.v("init");
        mAppID = StorageManager.getInstance().getOrSetLock(
                StorageManager.NAMESPACE_SETTING,
                KEY_APP_ID,
                UUID.randomUUID().toString());

        CoreLog.v("AppID=%s", mAppID);
    }

    public String getAppID() {
        return mAppID;
    }

}
