package com.politedroid.util;

import java.util.Iterator;

public class StringUtil {
    public static String join(Iterable<? extends CharSequence> s, CharSequence delimiter) {
        int capacity = 0;
        int delimLength = delimiter.length();
        Iterator<? extends CharSequence> iter = s.iterator();
        if (iter.hasNext()) {
            capacity += iter.next().length() + delimLength;
        }

        StringBuilder buffer = new StringBuilder(capacity);
        iter = s.iterator();
        if (iter.hasNext()) {
            buffer.append(iter.next());
            while (iter.hasNext()) {
                buffer.append(delimiter);
                buffer.append(iter.next());
            }
        }
        return buffer.toString();
    }
}
