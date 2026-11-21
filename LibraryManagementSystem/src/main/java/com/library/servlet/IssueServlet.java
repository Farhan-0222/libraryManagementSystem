package com.library.servlet;

import com.library.dao.*;
import com.library.model.User;
import com.library.util.*;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Issue a book: choose a student and an available book. Issue date = today, due date = today + loan days. */
@WebServlet("/issue")
public class IssueServlet extends HttpServlet {
    private final IssueDAO issueDAO = new IssueDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("students", studentDAO.getAll(""));
            req.setAttribute("books", bookDAO.getAvailable());
            req.setAttribute("recent", issueDAO.getRecent(8));
            req.setAttribute("loanDays", new SettingsDAO().getInt("loan_days", 14));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("active", "issue");
        req.setAttribute("pageTitle", "Issue Book");
        req.getRequestDispatcher("/WEB-INF/views/issue.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User staff = (User) req.getSession().getAttribute("user");
        int bookId = Param.toInt(req.getParameter("bookId"), 0);
        int studentId = Param.toInt(req.getParameter("studentId"), 0);
        if (bookId == 0 || studentId == 0) {
            Flash.error(req, "Select both a student and a book.");
        } else {
            String problem = issueDAO.issueBook(bookId, studentId, staff.getUserId());
            if (problem == null) Flash.success(req, "Book issued successfully.");
            else Flash.error(req, problem);
        }
        resp.sendRedirect(req.getContextPath() + "/issue");
    }
}
