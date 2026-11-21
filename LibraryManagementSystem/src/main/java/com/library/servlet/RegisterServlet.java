package com.library.servlet;

import com.library.dao.StudentDAO;
import com.library.model.Student;
import com.library.util.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Student self-registration. Staff accounts are created by the admin instead. */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Student s = new Student();
        s.setFullName(Param.text(req.getParameter("fullName")));
        s.setUsername(Param.text(req.getParameter("username")));
        s.setEmail(Param.text(req.getParameter("email")));
        s.setRollNo(Param.text(req.getParameter("rollNo")));
        s.setCourse(Param.text(req.getParameter("course")));
        s.setSemester(Param.toInt(req.getParameter("semester"), 0));
        s.setPhone(Param.text(req.getParameter("phone")));
        String password = Param.text(req.getParameter("password"));
        String confirm = Param.text(req.getParameter("confirm"));

        String error = null;
        if (s.getFullName().isEmpty() || s.getRollNo().isEmpty() || s.getCourse().isEmpty()) error = "Name, roll number and course are required.";
        else if (!Validator.isUsername(s.getUsername())) error = "Username must be 4-30 letters, digits or underscore.";
        else if (!Validator.isEmail(s.getEmail())) error = "Enter a valid email address.";
        else if (!Validator.isPhone(s.getPhone())) error = "Phone number must be 10 digits.";
        else if (s.getSemester() < 1 || s.getSemester() > 12) error = "Semester must be between 1 and 12.";
        else if (!Validator.isPassword(password)) error = "Password must be at least 6 characters.";
        else if (!password.equals(confirm)) error = "Passwords do not match.";

        if (error == null) {
            try {
                studentDAO.add(s, password);
                Flash.success(req, "Registration complete. You can log in now.");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            } catch (SQLIntegrityConstraintViolationException e) {
                error = "That username, email or roll number is already registered.";
            } catch (SQLException e) {
                error = "Database error: " + e.getMessage();
            }
        }
        req.setAttribute("error", error);
        req.setAttribute("form", s);
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }
}
