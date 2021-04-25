package io.github.idonans.core.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import io.github.idonans.core.Charsets;

/**
 * MD5 辅助类(小写32位)
 */
public class MD5Util {

    private static final String DEFAULT_MD5 = "";

    @NonNull
    public static String md5FilePath(@Nullable String filePath) {
        try {
            if (TextUtils.isEmpty(filePath)) {
                return DEFAULT_MD5;
            }

            return md5(new File(filePath));
        } catch (Throwable e) {
            e.printStackTrace();
            return DEFAULT_MD5;
        }
    }

    @NonNull
    public static String md5(@Nullable File file) {
        FileInputStream fis = null;
        DigestInputStream dis = null;
        try {
            if (file == null || !file.exists()) {
                return DEFAULT_MD5;
            }

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            dis = new DigestInputStream(fis, messageDigest);

            byte[] step = new byte[8 * 1024];
            int read;
            do {
                read = dis.read(step);
            } while (read != -1);

            byte[] data = messageDigest.digest();
            return HexUtil.toHexString(data);
        } catch (Throwable e) {
            e.printStackTrace();
            return DEFAULT_MD5;
        } finally {
            IOUtil.closeQuietly(dis);
            IOUtil.closeQuietly(fis);
        }
    }

    @NonNull
    public static String md5(@Nullable String str) {
        try {
            if (str == null) {
                return DEFAULT_MD5;
            }

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] data = messageDigest.digest(str.getBytes(Charsets.UTF8));
            return HexUtil.toHexString(data);
        } catch (Throwable e) {
            e.printStackTrace();
            return DEFAULT_MD5;
        }
    }


}
