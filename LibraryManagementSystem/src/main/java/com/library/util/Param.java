package com.library.util;

/** Small helpers for reading request parameters safely. */
public class Param {
    public static int toInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
    public static String text(String s) {
        return s == null ? "" : s.trim();
    }
}
