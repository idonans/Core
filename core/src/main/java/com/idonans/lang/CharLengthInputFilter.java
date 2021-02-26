package com.idonans.lang;

import android.text.InputFilter;
import android.text.Spanned;

import com.idonans.lang.util.CharLengthUtil;

public class CharLengthInputFilter implements InputFilter {

    private final int mMax;

    public CharLengthInputFilter(int max) {
        mMax = max;
    }

    public CharSequence filter(
            CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        boolean inputOverflow = false;

        try {
            int keep = mMax - CharLengthUtil.getCharLength(dest, 0, dstart) - CharLengthUtil.getCharLength(dest, dend, dest.length());
            if (keep <= 0) {
                inputOverflow = end > start;
                return "";
            }

            int sourceLength = CharLengthUtil.getCharLength(source, start, end);
            if (keep >= sourceLength) {
                return null; // keep original
            }

            inputOverflow = end > start;

            int endIndex = end;
            while (keep < sourceLength && endIndex > start) {
                char c = source.charAt(--endIndex);
                sourceLength -= CharLengthUtil.getCharLength(c);
                if (Character.isLowSurrogate(c)) {
                    c = source.charAt(--endIndex);
                    sourceLength -= CharLengthUtil.getCharLength(c);
                }
            }

            if (endIndex <= start) {
                return "";
            }

            return source.subSequence(start, endIndex);
        } finally {
            if (inputOverflow) {
                onInputOverflow();
            }
        }
    }

    public int getMax() {
        return mMax;
    }

    protected void onInputOverflow() {
    }

}
