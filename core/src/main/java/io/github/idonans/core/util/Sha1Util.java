package io.github.idonans.core.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import io.github.idonans.core.Charsets;

public class Sha1Util {

    private Sha1Util() {
    }

    private static final String DEFAULT_SHA1 = "";

    @NonNull
    public static String sha1FilePath(@Nullable String filePath) {
        try {
            if (TextUtils.isEmpty(filePath)) {
                return DEFAULT_SHA1;
            }

            return sha1(new File(filePath));
        } catch (Throwable e) {
            e.printStackTrace();
            return DEFAULT_SHA1;
        }
    }

    @NonNull
    public static String sha1(@Nullable File file) {
        FileInputStream fis = null;
        DigestInputStream dis = null;
        try {
            if (file == null || !file.exists()) {
                return DEFAULT_SHA1;
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
            return DEFAULT_SHA1;
        } finally {
            IOUtil.closeQuietly(dis);
            IOUtil.closeQuietly(fis);
        }
    }

    @NonNull
    public static String sha1(@Nullable String str) {
        try {
            if (str == null) {
                return DEFAULT_SHA1;
            }

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] data = messageDigest.digest(str.getBytes(Charsets.UTF8));
            return HexUtil.toHexString(data);
        } catch (Throwable e) {
            e.printStackTrace();
            return DEFAULT_SHA1;
        }
    }

}
