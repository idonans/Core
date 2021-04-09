package io.github.idonans.core.util;

import io.github.idonans.core.CoreLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 压缩与解压缩
 */
public final class ZipUtil {

    /**
     * 压缩
     */
    public static byte[] deflate(byte[] inputs) {
        ByteArrayOutputStream baos = null;
        DeflaterOutputStream dos = null;
        try {
            baos = new ByteArrayOutputStream(inputs.length);
            dos = new DeflaterOutputStream(baos);
            dos.write(inputs);
            dos.close();
            return baos.toByteArray();
        } catch (Throwable e) {
            CoreLog.e(e, "ZipUtil deflate fail");
            throw new RuntimeException(e);
        } finally {
            IOUtil.closeQuietly(dos);
            IOUtil.closeQuietly(baos);
        }
    }

    /**
     * 解压缩
     */
    public static byte[] inflate(byte[] inputs) {
        ByteArrayOutputStream baos = null;
        InflaterOutputStream ios = null;
        try {
            baos = new ByteArrayOutputStream(inputs.length);
            ios = new InflaterOutputStream(baos);
            ios.write(inputs);
            ios.close();
            return baos.toByteArray();
        } catch (Throwable e) {
            CoreLog.e(e, "ZipUtil inflate fail");
            throw new RuntimeException(e);
        } finally {
            IOUtil.closeQuietly(ios);
            IOUtil.closeQuietly(baos);
        }
    }

    /**
     * 解压缩
     */
    public static Map<String, byte[]> unzip(byte[] inputs) {
        final Map<String, byte[]> entryMap = new HashMap<>();
        ByteArrayInputStream bais = null;
        ZipInputStream zis = null;
        try {
            bais = new ByteArrayInputStream(inputs);
            zis = new ZipInputStream(bais);
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String name = zipEntry.getName();
                byte[] entryData = IOUtil.readAll(zis, null, null);
                entryMap.put(name, entryData);
            }
            return entryMap;
        } catch (Throwable e) {
            CoreLog.e(e, "ZipUtil unzip fail");
            throw new RuntimeException(e);
        } finally {
            IOUtil.closeQuietly(zis);
            IOUtil.closeQuietly(bais);
        }
    }

}
