package com.library.filter;

import com.library.model.User;
import com.library.util.Flash;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

/**
 * Runs before EVERY request. Two jobs:
 *  1. Authentication - if nobody is logged in, send the visitor to the login page.
 *  2. Authorization  - check that the user's role is allowed to open the page (role-based access control).
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getServletPath();

        // Stop the browser caching pages, so the Back button cannot show a page after logout.
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        if (isPublic(path)) { chain.doFilter(request, response); return; }

        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!isAllowed(path, user.getRole())) {
            Flash.error(req, "You do not have permission to open that page.");
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        return path.equals("") || path.equals("/") || path.equals("/index.jsp") || path.equals("/error.jsp")
            || path.equals("/login") || path.equals("/register")
            || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/");
    }

    private boolean isAllowed(String path, String role) {
        if (path.startsWith("/librarians") || path.startsWith("/settings")) {
            return role.equals("ADMIN");
        }
        if (path.startsWith("/books") || path.startsWith("/students") || path.startsWith("/issue")
                || path.startsWith("/return") || path.startsWith("/fines") || path.startsWith("/reports")) {
            return role.equals("ADMIN") || role.equals("LIBRARIAN");
        }
        return true;   // /dashboard, /catalog, /logout are open to every logged-in user
    }
}
