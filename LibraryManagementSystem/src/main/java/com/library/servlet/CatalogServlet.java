package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.util.Param;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Read-only book search for every role (students use this to find books and see availability). */
@WebServlet("/catalog")
public class CatalogServlet extends HttpServlet {
    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = Param.text(req.getParameter("q"));
        int categoryId = Param.toInt(req.getParameter("category"), 0);
        boolean onlyAvailable = "1".equals(req.getParameter("available"));
        try {
            var books = bookDAO.search(q, categoryId);
            if (onlyAvailable) books.removeIf(b -> b.getAvailableCopies() <= 0);
            req.setAttribute("books", books);
            req.setAttribute("categories", bookDAO.getCategories());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("q", q);
        req.setAttribute("selectedCategory", categoryId);
        req.setAttribute("onlyAvailable", onlyAvailable);
        req.setAttribute("active", "catalog");
        req.setAttribute("pageTitle", "Search Books");
        req.getRequestDispatcher("/WEB-INF/views/catalog.jsp").forward(req, resp);
    }
}
