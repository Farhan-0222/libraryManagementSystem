package com.library.dao;

import com.library.model.Student;
import com.library.util.DBConnection;
import com.library.util.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** All database work for students (users table + students table). */
public class StudentDAO {

    private static final String SELECT =
        "SELECT s.student_id, s.user_id, s.roll_no, s.course, s.semester, s.phone, u.full_name, u.email, u.username "
      + "FROM students s JOIN users u ON s.user_id = u.user_id ";

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setRollNo(rs.getString("roll_no"));
        s.setCourse(rs.getString("course"));
        s.setSemester(rs.getInt("semester"));
        s.setPhone(rs.getString("phone"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        s.setUsername(rs.getString("username"));
        return s;
    }

    public List<Student> getAll(String search) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = SELECT + "WHERE u.full_name LIKE ? OR s.roll_no LIKE ? OR u.email LIKE ? ORDER BY u.full_name";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + search + "%";
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Student getById(int studentId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT + "WHERE s.student_id = ?")) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public Student getByUserId(int userId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT + "WHERE s.user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    /** Creates the login (users) and the student profile in one transaction. */
    public void add(Student s, String password) throws SQLException {
        Connection con = DBConnection.getConnection();
        try {
            con.setAutoCommit(false);
            int userId;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users (username, password_hash, full_name, email, role) VALUES (?,?,?,?,'STUDENT')",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, s.getUsername());
                ps.setString(2, PasswordUtil.hash(password));
                ps.setString(3, s.getFullName());
                ps.setString(4, s.getEmail());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); userId = keys.getInt(1); }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO students (user_id, roll_no, course, semester, phone) VALUES (?,?,?,?,?)")) {
                ps.setInt(1, userId);
                ps.setString(2, s.getRollNo());
                ps.setString(3, s.getCourse());
                ps.setInt(4, s.getSemester());
                ps.setString(5, s.getPhone());
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

    public void update(Student s, String newPassword) throws SQLException {
        Connection con = DBConnection.getConnection();
        try {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement("UPDATE users SET full_name=?, email=? WHERE user_id=?")) {
                ps.setString(1, s.getFullName()); ps.setString(2, s.getEmail()); ps.setInt(3, s.getUserId());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE students SET roll_no=?, course=?, semester=?, phone=? WHERE student_id=?")) {
                ps.setString(1, s.getRollNo()); ps.setString(2, s.getCourse());
                ps.setInt(3, s.getSemester()); ps.setString(4, s.getPhone()); ps.setInt(5, s.getStudentId());
                ps.executeUpdate();
            }
            if (newPassword != null && !newPassword.isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE users SET password_hash=? WHERE user_id=?")) {
                    ps.setString(1, PasswordUtil.hash(newPassword)); ps.setInt(2, s.getUserId());
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

    /** Fails (SQLException) when the student has issue history - the foreign key protects the records. */
    public void delete(int studentId) throws SQLException {
        Student s = getById(studentId);
        if (s == null) return;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            ps.setInt(1, s.getUserId());
            ps.executeUpdate();
        }
    }
}
