package io.github.idonans.core.manager;

import androidx.annotation.Nullable;

import io.github.idonans.core.Constants;
import io.github.idonans.core.CoreLog;
import io.github.idonans.core.Singleton;
import io.github.idonans.core.thread.TaskQueue;
import io.github.idonans.core.thread.Threads;
import io.github.idonans.core.util.FileUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 临时文件管理器, 对于过期的临时文件会自动删除. 不同进程所使用的文件路径不同。
 */
public class TmpFileManager {

    private static final Singleton<TmpFileManager> INSTANCE =
            new Singleton<TmpFileManager>() {
                @Override
                protected TmpFileManager create() {
                    return new TmpFileManager();
                }
            };

    public static TmpFileManager getInstance() {
        return INSTANCE.get();
    }

    private static final long MAX_AGE = 10 * TimeUnit.DAYS.toMillis(1);
    private static final String TMP_DIR = Constants.GLOBAL_PREFIX + "tmp_files";

    private final TaskQueue mClearQueue = new TaskQueue(1);

    private static final int MAX_CLEAR_RETRY_COUNT = 5;

    private TmpFileManager() {
        CoreLog.v("init");
        clear();
    }

    @Nullable
    public File createNewTmpFileQuietly(String prefix, String suffix) {
        return FileUtil.createNewTmpFileQuietly(prefix, suffix, getTmpFileDir());
    }

    private File getTmpFileDir() {
        File extCacheDir = FileUtil.getAppCacheDir();
        if (extCacheDir == null) {
            return null;
        }

        return new File(extCacheDir, TMP_DIR);
    }

    /**
     * 清除过期临时文件
     */
    public void clear() {
        clear(0);
    }

    private void clear(final int retry) {
        if (retry > MAX_CLEAR_RETRY_COUNT) {
            CoreLog.e("retry %s times, abort.", retry);
            return;
        }

        final long delay = 10 * TimeUnit.MINUTES.toMillis(1) * (retry + 1);
        Threads.postUi(new Runnable() {
            @Override
            public void run() {
                Threads.postBackground(new Runnable() {
                    @Override
                    public void run() {
                        if (mClearQueue.getWaitCount() > 0) {
                            CoreLog.d("has other task for clear, ignore this.");
                            return;
                        }

                        mClearQueue.enqueue(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    clearInternal();
                                    CoreLog.d("clear success");
                                } catch (Throwable e) {
                                    CoreLog.e(e, "exception happen dur clear, retry later");
                                    clear(retry + 1);
                                }
                            }
                        });
                    }
                });
            }
        }, delay);
    }

    /**
     * 清除过期临时文件
     */
    private void clearInternal() throws Throwable {
        CoreLog.v("start clearInternal");

        File tmpFileDir = getTmpFileDir();
        if (tmpFileDir == null || !tmpFileDir.exists()) {
            CoreLog.v("clear tmp file dir not found %s", tmpFileDir);
            return;
        }

        File[] files = tmpFileDir.listFiles();
        if (files == null) {
            return;
        }

        int failToRemoveSize = 0;
        for (File file : files) {
            if (file == null) {
                continue;
            }

            if (!file.isFile()) {
                CoreLog.w("tmp file is not a file %s", file.getCanonicalPath());
            }

            if (file.lastModified() + MAX_AGE < System.currentTimeMillis()) {
                CoreLog.v("remove expire tmp file %s", file);
                if (!FileUtil.deleteFileQuietly(file)) {
                    failToRemoveSize++;
                    CoreLog.e("fail to remove expire tmp file %s", file);
                }
            }
        }

        if (failToRemoveSize > 0) {
            throw new IllegalStateException("fail to remove " + failToRemoveSize + " expire tmp files");
        }
    }

}
