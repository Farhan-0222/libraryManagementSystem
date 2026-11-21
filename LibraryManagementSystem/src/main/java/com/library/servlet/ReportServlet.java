package com.library.servlet;

import com.library.dao.ReportDAO;
import com.library.dao.SettingsDAO;
import com.library.util.Param;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Reports. /reports?type=overdue shows a printable table; add &format=csv to download it for Excel. */
@WebServlet("/reports")
public class ReportServlet extends HttpServlet {
    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = Param.text(req.getParameter("type"));
        if (ReportDAO.titleOf(type) == null) type = "books";
        try {
            List<List<String>> rows = reportDAO.run(type, new SettingsDAO().getDouble("fine_per_day", 5));

            if ("csv".equals(req.getParameter("format"))) {
                resp.setContentType("text/csv; charset=UTF-8");
                resp.setHeader("Content-Disposition", "attachment; filename=\"" + type + "_report_" + LocalDate.now() + ".csv\"");
                PrintWriter out = resp.getWriter();
                out.print('\uFEFF');                                  // lets Excel read UTF-8 correctly
                for (List<String> row : rows) {
                    StringBuilder line = new StringBuilder();
                    for (String cell : row) {
                        if (line.length() > 0) line.append(',');
                        line.append('"').append(cell.replace("\"", "\"\"")).append('"');
                    }
                    out.println(line);
                }
                return;
            }
            req.setAttribute("head", rows.get(0));
            req.setAttribute("body", rows.subList(1, rows.size()));
            req.setAttribute("types", ReportDAO.TYPES);
            req.setAttribute("type", type);
            req.setAttribute("reportTitle", ReportDAO.titleOf(type));
            req.setAttribute("today", LocalDate.now().toString());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("active", "reports");
        req.setAttribute("pageTitle", "Reports");
        req.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(req, resp);
    }
}
