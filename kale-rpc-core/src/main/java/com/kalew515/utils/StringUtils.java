package com.kalew515.utils;

public class StringUtils {

    public static boolean isBlank (String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEquals (String s, String target) {
        if (isBlank(s)) return false;
        return s.equals(target);
    }
}
