package com.library.servlet;

import com.library.dao.SettingsDAO;
import com.library.dao.StudentDAO;
import com.library.dao.UserDAO;
import com.library.model.User;
import com.library.util.Param;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** GET /login shows the form. POST /login checks the username and password. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("user") != null) {       // already logged in
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = Param.text(req.getParameter("username"));
        String password = Param.text(req.getParameter("password"));

        if (username.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Enter both username and password.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }
        try {
            User user = userDAO.login(username, password);
            if (user == null) {
                req.setAttribute("error", "Wrong username or password.");
                req.setAttribute("lastUsername", username);
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
                return;
            }
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();                    // new session after login (prevents session fixation)
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("libraryName", new SettingsDAO().get("library_name", "Central Library"));
            if ("STUDENT".equals(user.getRole())) {
                session.setAttribute("student", new StudentDAO().getByUserId(user.getUserId()));
            }
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            req.setAttribute("error", "Cannot reach the database: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
