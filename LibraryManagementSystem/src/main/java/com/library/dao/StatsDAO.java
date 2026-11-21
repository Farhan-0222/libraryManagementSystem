package com.library.dao;

import com.library.util.DBConnection;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Numbers for the dashboard cards and charts - everything comes from MySQL, nothing is hard-coded. */
public class StatsDAO {

    private Number single(Connection con, String sql) throws SQLException {
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getObject(1) == null ? 0 : (Number) rs.getObject(1);
        }
    }

    public Map<String, Number> getCounts() throws SQLException {
        Map<String, Number> m = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection()) {
            m.put("totalBooks",     single(con, "SELECT COALESCE(SUM(total_copies),0) FROM books"));
            m.put("bookTitles",     single(con, "SELECT COUNT(*) FROM books"));
            m.put("availableBooks", single(con, "SELECT COALESCE(SUM(available_copies),0) FROM books"));
            m.put("issuedBooks",    single(con, "SELECT COUNT(*) FROM book_issues WHERE status='ISSUED'"));
            m.put("totalStudents",  single(con, "SELECT COUNT(*) FROM students"));
            m.put("totalLibrarians",single(con, "SELECT COUNT(*) FROM librarians"));
            m.put("overdueBooks",   single(con, "SELECT COUNT(*) FROM book_issues WHERE status='ISSUED' AND due_date < CURDATE()"));
            m.put("unpaidFines",    single(con, "SELECT COALESCE(SUM(amount),0) FROM fines WHERE status='UNPAID'"));
            m.put("collectedFines", single(con, "SELECT COALESCE(SUM(amount),0) FROM fines WHERE status='PAID'"));
        }
        return m;
    }

    /** Copies per category, for the bar chart. */
    public Map<String, Integer> getCategoryCounts() throws SQLException {
        Map<String, Integer> m = new LinkedHashMap<>();
        String sql = "SELECT c.name, COALESCE(SUM(b.total_copies),0) FROM categories c "
                   + "LEFT JOIN books b ON b.category_id = c.category_id GROUP BY c.category_id, c.name ORDER BY c.name";
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) m.put(rs.getString(1), rs.getInt(2));
        }
        return m;
    }

    /** Books issued per month for the last 6 months, for the line chart. */
    public Map<String, Integer> getMonthlyIssues() throws SQLException {
        Map<String, Integer> m = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(issue_date,'%b %Y') AS m, COUNT(*) FROM book_issues "
                   + "WHERE issue_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) "
                   + "GROUP BY YEAR(issue_date), MONTH(issue_date), m ORDER BY YEAR(issue_date), MONTH(issue_date)";
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) m.put(rs.getString(1), rs.getInt(2));
        }
        return m;
    }
}
