package com.library.dao;

import com.library.model.IssueRecord;
import com.library.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Issue, return and fine operations. Issue and return use transactions. */
public class IssueDAO {

    private final SettingsDAO settings = new SettingsDAO();

    // One SELECT used by every list: issue + book + student + return + fine, with overdue days worked out in SQL.
    private static final String SELECT =
        "SELECT bi.issue_id, bi.book_id, b.title, bi.student_id, u.full_name, s.roll_no, "
      + "bi.issue_date, bi.due_date, bi.return_date, bi.status, DATEDIFF(bi.due_date, CURDATE()) AS days_left, "
      + "CASE WHEN bi.status='ISSUED' THEN GREATEST(DATEDIFF(CURDATE(), bi.due_date), 0) "
      + "     ELSE COALESCE(r.overdue_days, 0) END AS overdue_days, "
      + "f.fine_id, f.amount AS fine_amount, f.status AS fine_status "
      + "FROM book_issues bi "
      + "JOIN books b ON bi.book_id = b.book_id "
      + "JOIN students s ON bi.student_id = s.student_id "
      + "JOIN users u ON s.user_id = u.user_id "
      + "LEFT JOIN book_returns r ON r.issue_id = bi.issue_id "
      + "LEFT JOIN fines f ON f.issue_id = bi.issue_id ";

    private IssueRecord map(ResultSet rs, double rate) throws SQLException {
        IssueRecord r = new IssueRecord();
        r.setIssueId(rs.getInt("issue_id"));
        r.setBookId(rs.getInt("book_id"));
        r.setBookTitle(rs.getString("title"));
        r.setStudentId(rs.getInt("student_id"));
        r.setStudentName(rs.getString("full_name"));
        r.setRollNo(rs.getString("roll_no"));
        r.setIssueDate(rs.getDate("issue_date"));
        r.setDueDate(rs.getDate("due_date"));
        r.setReturnDate(rs.getDate("return_date"));
        r.setStatus(rs.getString("status"));
        r.setOverdueDays(rs.getInt("overdue_days"));
        r.setDaysLeft(rs.getInt("days_left"));
        r.setFineId(rs.getInt("fine_id"));
        boolean hasFine = !rs.wasNull();
        if (hasFine) {
            r.setFineAmount(rs.getDouble("fine_amount"));      // saved fine (book already returned)
            r.setFineStatus(rs.getString("fine_status"));
        } else if ("ISSUED".equals(r.getStatus()) && r.getOverdueDays() > 0) {
            r.setFineAmount(r.getOverdueDays() * rate);        // running fine for a book not yet returned
            r.setFineStatus("RUNNING");
        }
        return r;
    }

    private List<IssueRecord> query(String tail, Object... params) throws SQLException {
        double rate = settings.getDouble("fine_per_day", 5);
        List<IssueRecord> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT + tail)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs, rate));
            }
        }
        return list;
    }

    public List<IssueRecord> getRecent(int limit) throws SQLException {
        return query("ORDER BY bi.issue_id DESC LIMIT ?", limit);
    }

    public List<IssueRecord> getAllHistory() throws SQLException {
        return query("ORDER BY bi.issue_id DESC");
    }

    public List<IssueRecord> getCurrentIssues() throws SQLException {
        return query("WHERE bi.status = 'ISSUED' ORDER BY bi.due_date");
    }

    public List<IssueRecord> getOverdue() throws SQLException {
        return query("WHERE bi.status = 'ISSUED' AND bi.due_date < CURDATE() ORDER BY bi.due_date");
    }

    public List<IssueRecord> getByStudent(int studentId) throws SQLException {
        return query("WHERE bi.student_id = ? ORDER BY bi.issue_id DESC", studentId);
    }

    /** Saved fines (created when late books were returned). */
    public List<IssueRecord> getSavedFines() throws SQLException {
        return query("WHERE f.fine_id IS NOT NULL ORDER BY (f.status = 'UNPAID') DESC, f.fine_id DESC");
    }

    /**
     * Issue a book. Returns null on success, otherwise a message that explains why it was refused.
     * Rules: copy must be available, student limit not reached, no unpaid fine, same book not already issued.
     */
    public String issueBook(int bookId, int studentId, int staffUserId) {
        int loanDays = settings.getInt("loan_days", 14);
        int maxBooks = settings.getInt("max_books", 3);
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM book_issues WHERE student_id=? AND status='ISSUED'")) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) >= maxBooks) { con.rollback(); return "Limit reached: the student already holds " + maxBooks + " books."; }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM book_issues WHERE student_id=? AND book_id=? AND status='ISSUED'")) {
                ps.setInt(1, studentId); ps.setInt(2, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) { con.rollback(); return "This student already has a copy of this book."; }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM fines f JOIN book_issues bi ON f.issue_id=bi.issue_id "
                  + "WHERE bi.student_id=? AND f.status='UNPAID'")) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) { con.rollback(); return "The student has an unpaid fine. Collect it before issuing a book."; }
                }
            }
            // The WHERE clause makes sure we never go below zero, even if two librarians click together.
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE books SET available_copies = available_copies - 1 WHERE book_id=? AND available_copies > 0")) {
                ps.setInt(1, bookId);
                if (ps.executeUpdate() == 0) { con.rollback(); return "No copy of this book is available right now."; }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO book_issues (book_id, student_id, issued_by, issue_date, due_date, status) "
                  + "VALUES (?, ?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL ? DAY), 'ISSUED')")) {
                ps.setInt(1, bookId); ps.setInt(2, studentId); ps.setInt(3, staffUserId); ps.setInt(4, loanDays);
                ps.executeUpdate();
            }
            con.commit();
            return null;
        } catch (SQLException e) {
            rollbackQuietly(con);
            return "Database error: " + e.getMessage();
        } finally {
            closeQuietly(con);
        }
    }

    /**
     * Accept a returned book. Returns the fine amount (0 if on time), or -1 if the issue was not found
     * or was already returned.
     */
    public double returnBook(int issueId, int staffUserId) {
        double rate = settings.getDouble("fine_per_day", 5);
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            int bookId, overdue;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT book_id, status, GREATEST(DATEDIFF(CURDATE(), due_date), 0) FROM book_issues "
                  + "WHERE issue_id = ? FOR UPDATE")) {
                ps.setInt(1, issueId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || !"ISSUED".equals(rs.getString(2))) { con.rollback(); return -1; }
                    bookId = rs.getInt(1);
                    overdue = rs.getInt(3);
                }
            }
            double fine = overdue * rate;                       // FINE = overdue days x rate per day
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE book_issues SET return_date = CURDATE(), status = 'RETURNED' WHERE issue_id = ?")) {
                ps.setInt(1, issueId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO book_returns (issue_id, return_date, received_by, overdue_days) VALUES (?, CURDATE(), ?, ?)")) {
                ps.setInt(1, issueId); ps.setInt(2, staffUserId); ps.setInt(3, overdue);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?")) {
                ps.setInt(1, bookId);
                ps.executeUpdate();
            }
            if (fine > 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO fines (issue_id, amount, status) VALUES (?, ?, 'UNPAID')")) {
                    ps.setInt(1, issueId); ps.setDouble(2, fine);
                    ps.executeUpdate();
                }
            }
            con.commit();
            return fine;
        } catch (SQLException e) {
            rollbackQuietly(con);
            e.printStackTrace();
            return -1;
        } finally {
            closeQuietly(con);
        }
    }

    public boolean payFine(int fineId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE fines SET status='PAID', paid_date=CURDATE() WHERE fine_id=? AND status='UNPAID'")) {
            ps.setInt(1, fineId);
            return ps.executeUpdate() > 0;
        }
    }

    private void rollbackQuietly(Connection con) { try { if (con != null) con.rollback(); } catch (SQLException ignored) { } }
    private void closeQuietly(Connection con)    { try { if (con != null) con.close(); }    catch (SQLException ignored) { } }
}
