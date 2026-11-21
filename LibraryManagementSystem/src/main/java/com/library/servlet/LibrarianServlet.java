package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.Librarian;
import com.library.util.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Librarian account management (admin only). */
@WebServlet("/librarians")
public class LibrarianServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = Param.text(req.getParameter("q"));
        try {
            req.setAttribute("librarians", userDAO.getLibrarians(q));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("q", q);
        req.setAttribute("active", "librarians");
        req.setAttribute("pageTitle", "Librarian Accounts");
        req.getRequestDispatcher("/WEB-INF/views/librarians.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = Param.text(req.getParameter("action"));
        try {
            if (action.equals("delete")) {
                userDAO.deleteUser(Param.toInt(req.getParameter("userId"), 0));
                Flash.success(req, "Librarian account deleted.");
            } else {
                Librarian l = new Librarian();
                l.setUserId(Param.toInt(req.getParameter("userId"), 0));
                l.setFullName(Param.text(req.getParameter("fullName")));
                l.setUsername(Param.text(req.getParameter("username")));
                l.setEmail(Param.text(req.getParameter("email")));
                l.setEmployeeCode(Param.text(req.getParameter("employeeCode")));
                l.setPhone(Param.text(req.getParameter("phone")));
                String password = Param.text(req.getParameter("password"));

                String error = null;
                if (l.getFullName().isEmpty() || l.getEmployeeCode().isEmpty()) error = "Name and employee code are required.";
                else if (!Validator.isEmail(l.getEmail())) error = "Enter a valid email address.";
                else if (!Validator.isPhone(l.getPhone())) error = "Phone number must be 10 digits.";
                else if (action.equals("add") && !Validator.isUsername(l.getUsername())) error = "Username must be 4-30 letters, digits or underscore.";
                else if (action.equals("add") && !Validator.isPassword(password)) error = "Password must be at least 6 characters.";
                else if (action.equals("update") && !password.isEmpty() && !Validator.isPassword(password)) error = "New password must be at least 6 characters.";

                if (error != null) Flash.error(req, error);
                else if (action.equals("add")) { userDAO.addLibrarian(l, password); Flash.success(req, "Librarian added: " + l.getFullName()); }
                else { userDAO.updateLibrarian(l, password); Flash.success(req, "Librarian updated: " + l.getFullName()); }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            Flash.error(req, "That username, email or employee code already exists.");
        } catch (SQLException e) {
            Flash.error(req, "Database error: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/librarians");
    }
}
