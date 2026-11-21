package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.util.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Year;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Book management (admin + librarian): list/search/filter (GET) and add/update/delete/addCategory (POST). */
@WebServlet("/books")
public class BookServlet extends HttpServlet {
    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = Param.text(req.getParameter("q"));
        int categoryId = Param.toInt(req.getParameter("category"), 0);
        try {
            req.setAttribute("books", bookDAO.search(q, categoryId));
            req.setAttribute("categories", bookDAO.getCategories());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("q", q);
        req.setAttribute("selectedCategory", categoryId);
        req.setAttribute("active", "books");
        req.setAttribute("pageTitle", "Book Management");
        req.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = Param.text(req.getParameter("action"));
        try {
            switch (action) {
                case "add":
                case "update": {
                    Book b = new Book();
                    b.setBookId(Param.toInt(req.getParameter("bookId"), 0));
                    b.setIsbn(Param.text(req.getParameter("isbn")));
                    b.setTitle(Param.text(req.getParameter("title")));
                    b.setAuthor(Param.text(req.getParameter("author")));
                    b.setPublisher(Param.text(req.getParameter("publisher")));
                    b.setCategoryId(Param.toInt(req.getParameter("categoryId"), 0));
                    b.setPublicationYear(Param.toInt(req.getParameter("publicationYear"), 0));
                    b.setTotalCopies(Param.toInt(req.getParameter("totalCopies"), -1));
                    String cover = Param.text(req.getParameter("coverUrl"));
                    b.setCoverUrl(cover.isEmpty() ? null : cover);

                    String error = null;
                    if (b.getTitle().isEmpty() || b.getAuthor().isEmpty()) error = "Title and author are required.";
                    else if (!Validator.isIsbn(b.getIsbn())) error = "ISBN must be 10-17 digits (hyphens allowed).";
                    else if (b.getCategoryId() <= 0) error = "Choose a category.";
                    else if (b.getPublicationYear() < 1000 || b.getPublicationYear() > Year.now().getValue() + 1) error = "Enter a valid publication year.";
                    else if (b.getTotalCopies() < 0) error = "Total copies must be 0 or more.";
                    if (error != null) { Flash.error(req, error); break; }

                    if (action.equals("add")) {
                        bookDAO.add(b);
                        Flash.success(req, "Book added: " + b.getTitle());
                    } else if (bookDAO.update(b)) {
                        Flash.success(req, "Book updated: " + b.getTitle());
                    } else {
                        Flash.error(req, "Total copies cannot be lower than the copies currently issued.");
                    }
                    break;
                }
                case "delete":
                    bookDAO.delete(Param.toInt(req.getParameter("bookId"), 0));
                    Flash.success(req, "Book deleted.");
                    break;
                case "addCategory": {
                    String name = Param.text(req.getParameter("name"));
                    if (name.isEmpty()) Flash.error(req, "Category name is required.");
                    else { bookDAO.addCategory(name); Flash.success(req, "Category added: " + name); }
                    break;
                }
                default:
                    Flash.error(req, "Unknown action.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            if (action.equals("delete")) Flash.error(req, "This book has issue history, so it cannot be deleted.");
            else Flash.error(req, "That ISBN or category already exists.");
        } catch (SQLException e) {
            Flash.error(req, "Database error: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/books");
    }
}
