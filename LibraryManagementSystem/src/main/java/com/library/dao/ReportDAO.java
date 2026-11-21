package com.library.dao;

import com.library.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs one SQL query per report type. The first row of the result holds the column headings,
 * so one JSP page (and the CSV export) can show any report.
 */
public class ReportDAO {

    public static final String[][] TYPES = {
        {"books", "Total Books Report"},
        {"available", "Available Books Report"},
        {"issued", "Issued Books Report"},
        {"returned", "Returned Books Report"},
        {"overdue", "Overdue Books Report"},
        {"borrowing", "Student Borrowing Report"},
        {"fines", "Fine Collection Report"}
    };

    public static String titleOf(String type) {
        for (String[] t : TYPES) if (t[0].equals(type)) return t[1];
        return null;
    }

    private String sqlFor(String type) {
        String bookBase = "SELECT b.book_id AS 'Book ID', b.isbn AS 'ISBN', b.title AS 'Title', b.author AS 'Author', "
            + "c.name AS 'Category', b.total_copies AS 'Total', b.available_copies AS 'Available' "
            + "FROM books b JOIN categories c ON b.category_id = c.category_id ";
        String issueBase = "SELECT bi.issue_id AS 'Issue ID', b.title AS 'Book', u.full_name AS 'Student', s.roll_no AS 'Roll No', "
            + "bi.issue_date AS 'Issued On', bi.due_date AS 'Due Date' ";
        String issueJoin = "FROM book_issues bi JOIN books b ON bi.book_id=b.book_id JOIN students s ON bi.student_id=s.student_id "
            + "JOIN users u ON s.user_id=u.user_id ";
        switch (type) {
            case "books":     return bookBase + "ORDER BY b.title";
            case "available": return bookBase + "WHERE b.available_copies > 0 ORDER BY b.title";
            case "issued":    return issueBase + ", DATEDIFF(CURDATE(), bi.issue_date) AS 'Days Held' " + issueJoin
                                   + "WHERE bi.status='ISSUED' ORDER BY bi.due_date";
            case "returned":  return issueBase + ", bi.return_date AS 'Returned On', r.overdue_days AS 'Overdue Days' " + issueJoin
                                   + "JOIN book_returns r ON r.issue_id=bi.issue_id ORDER BY bi.return_date DESC";
            case "overdue":   return issueBase + ", DATEDIFF(CURDATE(), bi.due_date) AS 'Days Overdue', "
                                   + "DATEDIFF(CURDATE(), bi.due_date) * ? AS 'Running Fine (Rs)' " + issueJoin
                                   + "WHERE bi.status='ISSUED' AND bi.due_date < CURDATE() ORDER BY bi.due_date";
            case "borrowing": return "SELECT s.roll_no AS 'Roll No', u.full_name AS 'Student', s.course AS 'Course', "
                                   + "COUNT(bi.issue_id) AS 'Total Borrowed', COALESCE(SUM(bi.status='ISSUED'),0) AS 'Currently Holding' "
                                   + "FROM students s JOIN users u ON s.user_id=u.user_id "
                                   + "LEFT JOIN book_issues bi ON bi.student_id=s.student_id GROUP BY s.student_id, s.roll_no, u.full_name, s.course "
                                   + "ORDER BY u.full_name";
            case "fines":     return "SELECT f.fine_id AS 'Fine ID', u.full_name AS 'Student', b.title AS 'Book', "
                                   + "f.amount AS 'Amount (Rs)', f.status AS 'Status', f.paid_date AS 'Paid On' "
                                   + "FROM fines f JOIN book_issues bi ON f.issue_id=bi.issue_id JOIN students s ON bi.student_id=s.student_id "
                                   + "JOIN users u ON s.user_id=u.user_id JOIN books b ON bi.book_id=b.book_id ORDER BY f.fine_id DESC";
            default:          return null;
        }
    }

    /** Row 0 = headings, rows 1..n = data (everything converted to text). */
    public List<List<String>> run(String type, double fineRate) throws SQLException {
        String sql = sqlFor(type);
        List<List<String>> rows = new ArrayList<>();
        if (sql == null) return rows;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (sql.contains("?")) ps.setDouble(1, fineRate);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();
                List<String> head = new ArrayList<>();
                for (int i = 1; i <= cols; i++) head.add(md.getColumnLabel(i));
                rows.add(head);
                while (rs.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= cols; i++) {
                        String v = rs.getString(i);
                        row.add(v == null ? "-" : v);
                    }
                    rows.add(row);
                }
            }
        }
        return rows;
    }
}
