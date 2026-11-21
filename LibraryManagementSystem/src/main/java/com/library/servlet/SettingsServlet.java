package com.library.servlet;

import com.library.dao.SettingsDAO;
import com.library.util.*;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** System settings (admin only): fine per day, loan period, max books per student, library name. */
@WebServlet("/settings")
public class SettingsServlet extends HttpServlet {
    private final SettingsDAO settings = new SettingsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("cfg", settings.getAll());
        req.setAttribute("active", "settings");
        req.setAttribute("pageTitle", "System Settings");
        req.getRequestDispatcher("/WEB-INF/views/settings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = Param.text(req.getParameter("library_name"));
        int loanDays = Param.toInt(req.getParameter("loan_days"), 0);
        int maxBooks = Param.toInt(req.getParameter("max_books"), 0);
        double fine;
        try { fine = Double.parseDouble(Param.text(req.getParameter("fine_per_day"))); } catch (NumberFormatException e) { fine = -1; }

        if (name.isEmpty() || fine < 0 || fine > 1000 || loanDays < 1 || loanDays > 90 || maxBooks < 1 || maxBooks > 20) {
            Flash.error(req, "Check the values: fine 0-1000, loan days 1-90, max books 1-20, and a library name.");
        } else {
            try {
                settings.set("library_name", name);
                settings.set("fine_per_day", String.valueOf(fine));
                settings.set("loan_days", String.valueOf(loanDays));
                settings.set("max_books", String.valueOf(maxBooks));
                req.getSession().setAttribute("libraryName", name);
                Flash.success(req, "Settings saved.");
            } catch (SQLException e) {
                Flash.error(req, "Database error: " + e.getMessage());
            }
        }
        resp.sendRedirect(req.getContextPath() + "/settings");
    }
}
