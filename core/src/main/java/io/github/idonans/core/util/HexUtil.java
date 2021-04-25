package io.github.idonans.core.util;

public class HexUtil {

    private static final char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private HexUtil() {
    }

    public static String toHexString(byte[] data) {
        char[] result = new char[data.length * 2];
        int i = 0;
        for (byte b : data) {
            result[i++] = HEX_DIGITS[(b >> 4) & 0xf];
            result[i++] = HEX_DIGITS[b & 0xf];
        }
        return String.valueOf(result);
    }

}
