package com.idonans.lang.manager;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.idonans.lang.Constants;
import com.idonans.lang.Singleton;
import com.idonans.lang.db.SimpleDB;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * 不支持跨进程，不同进程所在存储空间不同
 */
public class StorageManager {

    public static final String NAMESPACE_SETTING = Constants.GLOBAL_PREFIX + "setting";
    public static final String NAMESPACE_CACHE = Constants.GLOBAL_PREFIX + "cache";

    private static final Singleton<StorageManager> sInstance =
            new Singleton<StorageManager>() {
                @Override
                protected StorageManager create() {
                    return new StorageManager();
                }
            };

    public static StorageManager getInstance() {
        return sInstance.get();
    }

    private final Map<String, SimpleDB> mProviders = new HashMap<>();

    private final Object mGetOrSetLock = new Object();

    private StorageManager() {
        Timber.v("init");
    }

    public void set(String namespace, String key, String value) {
        getTarget(namespace).set(key, value);
    }

    public String get(String namespace, String key) {
        return getTarget(namespace).get(key);
    }

    public String getOrSetLock(String namespace, String key, String setValue) {
        String value;
        SimpleDB target = getTarget(namespace);
        synchronized (mGetOrSetLock) {
            value = target.get(key);
            if (TextUtils.isEmpty(value)) {
                value = setValue;
                target.set(key, value);
            }
        }
        return value;
    }

    public void printAllRows(String namespace) {
        getTarget(namespace).printAllRows();
    }

    @NonNull
    private SimpleDB getTarget(String namespace) {
        namespace = checkNamespace(namespace);

        SimpleDB db;
        boolean trim = false;
        synchronized (mProviders) {
            db = mProviders.get(namespace);
            if (db == null) {
                db = new SimpleDB(namespace);
                mProviders.put(namespace, db);
                trim = true;
            }
        }
        if (trim) {
            db.trim(MAX_ROWS);
        }
        return db;
    }

    private static String checkNamespace(String namespace) {
        if (TextUtils.isEmpty(namespace)) {
            throw new IllegalArgumentException("need namespace, like StorageManager#NAMESPACE_*");
        }
        return namespace;
    }

    private static final int MAX_ROWS = 5000;

}
