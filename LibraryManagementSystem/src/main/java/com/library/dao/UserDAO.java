package com.library.dao;

import com.library.model.Librarian;
import com.library.model.User;
import com.library.util.DBConnection;
import com.library.util.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Login check and librarian account management. */
public class UserDAO {

    /** Returns the User if username + password match an ACTIVE account, otherwise null. */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, full_name, email, role, status FROM users "
                   + "WHERE username = ? AND password_hash = ? AND status = 'ACTIVE'";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setFullName(rs.getString("full_name"));
                    u.setEmail(rs.getString("email"));
                    u.setRole(rs.getString("role"));
                    u.setStatus(rs.getString("status"));
                    return u;
                }
            }
        }
        return null;
    }

    public List<Librarian> getLibrarians(String search) throws SQLException {
        List<Librarian> list = new ArrayList<>();
        String sql = "SELECT l.librarian_id, l.user_id, l.employee_code, l.phone, u.full_name, u.email, u.username "
                   + "FROM librarians l JOIN users u ON l.user_id = u.user_id "
                   + "WHERE u.full_name LIKE ? OR u.username LIKE ? OR l.employee_code LIKE ? ORDER BY u.full_name";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + search + "%";
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Librarian l = new Librarian();
                    l.setLibrarianId(rs.getInt("librarian_id"));
                    l.setUserId(rs.getInt("user_id"));
                    l.setEmployeeCode(rs.getString("employee_code"));
                    l.setPhone(rs.getString("phone"));
                    l.setFullName(rs.getString("full_name"));
                    l.setEmail(rs.getString("email"));
                    l.setUsername(rs.getString("username"));
                    list.add(l);
                }
            }
        }
        return list;
    }

    /** Adds a users row and a librarians row inside ONE transaction (both succeed or both fail). */
    public void addLibrarian(Librarian l, String password) throws SQLException {
        Connection con = DBConnection.getConnection();
        try {
            con.setAutoCommit(false);
            int userId;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users (username, password_hash, full_name, email, role) VALUES (?,?,?,?,'LIBRARIAN')",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, l.getUsername());
                ps.setString(2, PasswordUtil.hash(password));
                ps.setString(3, l.getFullName());
                ps.setString(4, l.getEmail());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); userId = keys.getInt(1); }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO librarians (user_id, employee_code, phone) VALUES (?,?,?)")) {
                ps.setInt(1, userId);
                ps.setString(2, l.getEmployeeCode());
                ps.setString(3, l.getPhone());
                ps.executeUpdate();
            }
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.close();
        }
    }

    /** Updates a librarian. If newPassword is empty the old password is kept. */
    public void updateLibrarian(Librarian l, String newPassword) throws SQLException {
        Connection con = DBConnection.getConnection();
        try {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE users SET full_name=?, email=? WHERE user_id=?")) {
                ps.setString(1, l.getFullName()); ps.setString(2, l.getEmail()); ps.setInt(3, l.getUserId());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE librarians SET employee_code=?, phone=? WHERE user_id=?")) {
                ps.setString(1, l.getEmployeeCode()); ps.setString(2, l.getPhone()); ps.setInt(3, l.getUserId());
                ps.executeUpdate();
            }
            if (newPassword != null && !newPassword.isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE users SET password_hash=? WHERE user_id=?")) {
                    ps.setString(1, PasswordUtil.hash(newPassword)); ps.setInt(2, l.getUserId());
                    ps.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.close();
        }
    }

    /** Deleting the users row also deletes the librarians row (ON DELETE CASCADE). */
    public void deleteUser(int userId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}
