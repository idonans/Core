package com.idonans.core;

import android.net.Uri;

import androidx.core.content.FileProvider;

import com.idonans.core.util.ContextUtil;

import java.io.File;

public class CommonFileProvider extends FileProvider {

    public static Uri getUriForFile(File file) {
        return FileProvider.getUriForFile(ContextUtil.getContext(), getAuthority(), file);
    }

    public static String getAuthority() {
        return Constants.GLOBAL_PREFIX + ContextUtil.getContext().getPackageName() + ".CommonFileProvider";
    }

}
