package com.library.util;

import javax.servlet.http.HttpServletRequest;

/** One-time success/error messages shown after a redirect (see header.jsp). */
public class Flash {
    public static void success(HttpServletRequest req, String msg) { set(req, "success", msg); }
    public static void error(HttpServletRequest req, String msg)   { set(req, "danger", msg); }
    private static void set(HttpServletRequest req, String type, String msg) {
        req.getSession().setAttribute("flashType", type);
        req.getSession().setAttribute("flashMsg", msg);
    }
}
