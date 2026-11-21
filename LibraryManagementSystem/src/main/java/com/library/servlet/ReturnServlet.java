package com.library.servlet;

import com.library.dao.IssueDAO;
import com.library.model.IssueRecord;
import com.library.model.User;
import com.library.util.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Accept returned books. Overdue days and the fine are calculated by IssueDAO.returnBook(). */
@WebServlet("/return")
public class ReturnServlet extends HttpServlet {
    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = Param.text(req.getParameter("q")).toLowerCase();
        try {
            List<IssueRecord> list = issueDAO.getCurrentIssues();
            if (!q.isEmpty()) {
                list = list.stream().filter(r -> r.getStudentName().toLowerCase().contains(q)
                        || r.getRollNo().toLowerCase().contains(q)
                        || r.getBookTitle().toLowerCase().contains(q)).collect(Collectors.toList());
            }
            req.setAttribute("records", list);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("q", q);
        req.setAttribute("active", "return");
        req.setAttribute("pageTitle", "Return Book");
        req.getRequestDispatcher("/WEB-INF/views/return.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User staff = (User) req.getSession().getAttribute("user");
        double fine = issueDAO.returnBook(Param.toInt(req.getParameter("issueId"), 0), staff.getUserId());
        if (fine < 0) Flash.error(req, "Could not process the return. It may already be returned.");
        else if (fine == 0) Flash.success(req, "Book returned on time. No fine.");
        else Flash.success(req, "Book returned late. Fine of Rs " + String.format("%.2f", fine) + " has been recorded.");
        resp.sendRedirect(req.getContextPath() + "/return");
    }
}
