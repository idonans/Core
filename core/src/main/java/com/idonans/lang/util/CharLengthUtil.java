package com.idonans.lang.util;

public class CharLengthUtil {

    private CharLengthUtil() {
    }

    public static int getCharLength(char c) {
        if (c <= 127) {
            return 1;
        } else {
            return 2;
        }
    }

    public static int getCharLength(CharSequence input) {
        if (input == null) {
            return 0;
        }
        return getCharLength(input, 0, input.length());
    }

    public static int getCharLength(CharSequence input, int start, int end) {
        int charLength = 0;
        if (input != null) {
            for (int i = start; i < end; i++) {
                char c = input.charAt(i);
                charLength += getCharLength(c);
            }
        }

        return charLength;
    }

}
