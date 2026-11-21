package com.library.servlet;

import com.library.dao.*;
import com.library.util.*;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Fine management: unpaid/paid fines, running fines of overdue books, and marking a fine as paid. */
@WebServlet("/fines")
public class FineServlet extends HttpServlet {
    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("fines", issueDAO.getSavedFines());
            req.setAttribute("overdue", issueDAO.getOverdue());
            req.setAttribute("stats", new StatsDAO().getCounts());
            req.setAttribute("rate", new SettingsDAO().getDouble("fine_per_day", 5));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("active", "fines");
        req.setAttribute("pageTitle", "Fine Management");
        req.getRequestDispatcher("/WEB-INF/views/fines.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (issueDAO.payFine(Param.toInt(req.getParameter("fineId"), 0))) Flash.success(req, "Fine marked as paid.");
            else Flash.error(req, "That fine was not found or is already paid.");
        } catch (SQLException e) {
            Flash.error(req, "Database error: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/fines");
    }
}
