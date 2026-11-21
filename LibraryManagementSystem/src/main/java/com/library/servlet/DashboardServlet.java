package com.library.servlet;

import com.library.dao.*;
import com.library.model.*;
import com.library.util.ChartUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Shows the admin/librarian dashboard (statistics + charts) or the student's personal dashboard. */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private final StatsDAO statsDAO = new StatsDAO();
    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        req.setAttribute("active", "dashboard");
        try {
            if ("STUDENT".equals(user.getRole())) {
                Student me = (Student) req.getSession().getAttribute("student");
                List<IssueRecord> all = issueDAO.getByStudent(me.getStudentId());
                int current = 0, overdue = 0;
                double fineDue = 0;
                for (IssueRecord r : all) {
                    if ("ISSUED".equals(r.getStatus())) current++;
                    if ("ISSUED".equals(r.getStatus()) && r.getOverdueDays() > 0) overdue++;
                    if ("UNPAID".equals(r.getFineStatus()) || "RUNNING".equals(r.getFineStatus())) fineDue += r.getFineAmount();
                }
                req.setAttribute("pageTitle", "My Dashboard");
                req.setAttribute("records", all);
                req.setAttribute("currentCount", current);
                req.setAttribute("overdueCount", overdue);
                req.setAttribute("fineDue", fineDue);
                req.setAttribute("totalBorrowed", all.size());
                req.getRequestDispatcher("/WEB-INF/views/my.jsp").forward(req, resp);
            } else {
                req.setAttribute("pageTitle", "Dashboard");
                req.setAttribute("stats", statsDAO.getCounts());
                req.setAttribute("recent", issueDAO.getRecent(8));
                req.setAttribute("catLabels", ChartUtil.labels(statsDAO.getCategoryCounts().keySet()));
                req.setAttribute("catValues", ChartUtil.numbers(statsDAO.getCategoryCounts().values()));
                req.setAttribute("monthLabels", ChartUtil.labels(statsDAO.getMonthlyIssues().keySet()));
                req.setAttribute("monthValues", ChartUtil.numbers(statsDAO.getMonthlyIssues().values()));
                req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
