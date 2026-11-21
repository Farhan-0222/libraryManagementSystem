package com.library.util;

/** Server-side form validation (the browser also validates, but the server must never trust the browser). */
public class Validator {
    public static boolean isEmail(String s)    { return s != null && s.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"); }
    public static boolean isPhone(String s)    { return s == null || s.isEmpty() || s.matches("^[0-9]{10}$"); }
    public static boolean isUsername(String s) { return s != null && s.matches("^[A-Za-z0-9_]{4,30}$"); }
    public static boolean isIsbn(String s)     { return s != null && s.matches("^[0-9-]{10,17}$"); }
    public static boolean isPassword(String s) { return s != null && s.length() >= 6; }
}
