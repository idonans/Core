package io.github.idonans.core;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexInputFilter implements InputFilter {

    private final Pattern mPattern;

    public RegexInputFilter(String regex) {
        this(Pattern.compile(regex));
    }

    public RegexInputFilter(Pattern pattern) {
        mPattern = pattern;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = mPattern.matcher(source);
        if (!matcher.matches()) {
            return "";
        }

        return null;
    }

}
