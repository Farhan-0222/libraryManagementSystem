package com.library.servlet;

import com.library.dao.IssueDAO;
import com.library.dao.StudentDAO;
import com.library.model.Student;
import com.library.util.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Student management (admin + librarian): list/search, borrowing history (GET), add/update/delete (POST). */
@WebServlet("/students")
public class StudentServlet extends HttpServlet {
    private final StudentDAO studentDAO = new StudentDAO();
    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("active", "students");
        try {
            int historyId = Param.toInt(req.getParameter("history"), 0);
            if (historyId > 0) {                                         // /students?history=3
                Student s = studentDAO.getById(historyId);
                if (s == null) { resp.sendRedirect(req.getContextPath() + "/students"); return; }
                req.setAttribute("student", s);
                req.setAttribute("records", issueDAO.getByStudent(historyId));
                req.setAttribute("pageTitle", "Borrowing History");
                req.getRequestDispatcher("/WEB-INF/views/student-history.jsp").forward(req, resp);
                return;
            }
            String q = Param.text(req.getParameter("q"));
            req.setAttribute("students", studentDAO.getAll(q));
            req.setAttribute("q", q);
            req.setAttribute("pageTitle", "Student Management");
            req.getRequestDispatcher("/WEB-INF/views/students.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = Param.text(req.getParameter("action"));
        try {
            if (action.equals("delete")) {
                studentDAO.delete(Param.toInt(req.getParameter("studentId"), 0));
                Flash.success(req, "Student deleted.");
            } else if (action.equals("add") || action.equals("update")) {
                Student s = new Student();
                s.setStudentId(Param.toInt(req.getParameter("studentId"), 0));
                s.setUserId(Param.toInt(req.getParameter("userId"), 0));
                s.setFullName(Param.text(req.getParameter("fullName")));
                s.setUsername(Param.text(req.getParameter("username")));
                s.setEmail(Param.text(req.getParameter("email")));
                s.setRollNo(Param.text(req.getParameter("rollNo")));
                s.setCourse(Param.text(req.getParameter("course")));
                s.setSemester(Param.toInt(req.getParameter("semester"), 0));
                s.setPhone(Param.text(req.getParameter("phone")));
                String password = Param.text(req.getParameter("password"));

                String error = null;
                if (s.getFullName().isEmpty() || s.getRollNo().isEmpty() || s.getCourse().isEmpty()) error = "Name, roll number and course are required.";
                else if (!Validator.isEmail(s.getEmail())) error = "Enter a valid email address.";
                else if (!Validator.isPhone(s.getPhone())) error = "Phone number must be 10 digits.";
                else if (s.getSemester() < 1 || s.getSemester() > 12) error = "Semester must be between 1 and 12.";
                else if (action.equals("add") && !Validator.isUsername(s.getUsername())) error = "Username must be 4-30 letters, digits or underscore.";
                else if (action.equals("add") && !Validator.isPassword(password)) error = "Password must be at least 6 characters.";
                else if (action.equals("update") && !password.isEmpty() && !Validator.isPassword(password)) error = "New password must be at least 6 characters.";

                if (error != null) Flash.error(req, error);
                else if (action.equals("add")) { studentDAO.add(s, password); Flash.success(req, "Student registered: " + s.getFullName()); }
                else { studentDAO.update(s, password); Flash.success(req, "Student updated: " + s.getFullName()); }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            if (action.equals("delete")) Flash.error(req, "This student has borrowing history, so the record cannot be deleted.");
            else Flash.error(req, "That username, email or roll number already exists.");
        } catch (SQLException e) {
            Flash.error(req, "Database error: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/students");
    }
}
