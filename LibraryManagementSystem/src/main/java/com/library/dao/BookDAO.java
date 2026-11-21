package com.library.dao;

import com.library.model.Book;
import com.library.model.Category;
import com.library.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** All database work for books and categories. */
public class BookDAO {

    private Book map(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setBookId(rs.getInt("book_id"));
        b.setIsbn(rs.getString("isbn"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setPublisher(rs.getString("publisher"));
        b.setCategoryId(rs.getInt("category_id"));
        b.setCategoryName(rs.getString("category_name"));
        b.setPublicationYear(rs.getInt("publication_year"));
        b.setTotalCopies(rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        b.setCoverUrl(rs.getString("cover_url"));
        return b;
    }

    private static final String SELECT =
        "SELECT b.*, c.name AS category_name FROM books b JOIN categories c ON b.category_id = c.category_id ";

    /** Search by title, author, ISBN or category name; categoryId 0 means "all categories". */
    public List<Book> search(String text, int categoryId) throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = SELECT + "WHERE (b.title LIKE ? OR b.author LIKE ? OR b.isbn LIKE ? OR c.name LIKE ?) "
                   + "AND (? = 0 OR b.category_id = ?) ORDER BY b.title";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + text + "%";
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like); ps.setString(4, like);
            ps.setInt(5, categoryId); ps.setInt(6, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    /** Only books that still have copies on the shelf (used by the issue form). */
    public List<Book> getAvailable() throws SQLException {
        List<Book> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT + "WHERE b.available_copies > 0 ORDER BY b.title")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void add(Book b) throws SQLException {
        String sql = "INSERT INTO books (isbn,title,author,publisher,category_id,publication_year,total_copies,available_copies,cover_url) "
                   + "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getIsbn());
            ps.setString(2, b.getTitle());
            ps.setString(3, b.getAuthor());
            ps.setString(4, b.getPublisher());
            ps.setInt(5, b.getCategoryId());
            ps.setInt(6, b.getPublicationYear());
            ps.setInt(7, b.getTotalCopies());
            ps.setInt(8, b.getTotalCopies());          // a new book: all copies are available
            ps.setString(9, b.getCoverUrl());
            ps.executeUpdate();
        }
    }

    /**
     * Updates a book. When total copies change, available copies change by the same amount.
     * Returns false if that would make available copies negative (more copies are issued than the new total).
     * NOTE: MySQL evaluates SET from left to right, so available_copies must come BEFORE total_copies.
     */
    public boolean update(Book b) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies + (? - total_copies), total_copies = ?, "
                   + "isbn=?, title=?, author=?, publisher=?, category_id=?, publication_year=?, cover_url=? "
                   + "WHERE book_id = ? AND available_copies + (? - total_copies) >= 0";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getTotalCopies());
            ps.setInt(2, b.getTotalCopies());
            ps.setString(3, b.getIsbn());
            ps.setString(4, b.getTitle());
            ps.setString(5, b.getAuthor());
            ps.setString(6, b.getPublisher());
            ps.setInt(7, b.getCategoryId());
            ps.setInt(8, b.getPublicationYear());
            ps.setString(9, b.getCoverUrl());
            ps.setInt(10, b.getBookId());
            ps.setInt(11, b.getTotalCopies());
            return ps.executeUpdate() > 0;
        }
    }

    /** Throws SQLException if the book has issue history (foreign key protects the records). */
    public void delete(int bookId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM books WHERE book_id = ?")) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        }
    }

    public List<Category> getCategories() throws SQLException {
        List<Category> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT category_id, name FROM categories ORDER BY name")) {
            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt(1));
                c.setName(rs.getString(2));
                list.add(c);
            }
        }
        return list;
    }

    public void addCategory(String name) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO categories (name) VALUES (?)")) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }
}
