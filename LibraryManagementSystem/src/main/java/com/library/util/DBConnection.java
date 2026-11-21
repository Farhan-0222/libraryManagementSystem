package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection for local development and cloud deployment.
 * Cloud settings are supplied through DB_URL, DB_USER and DB_PASSWORD.
 */
public class DBConnection {

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        // Keep the original local MySQL setup working when cloud variables are absent.
        if (url == null || url.trim().isEmpty()) {
            url = "jdbc:mysql://localhost:3306/library_db"
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata";
            user = (user == null || user.isEmpty()) ? "root" : user;
            password = (password == null) ? "root" : password;
        } else {
            if (user == null || user.trim().isEmpty() || password == null) {
                throw new SQLException("Cloud database configuration is incomplete. Set DB_URL, DB_USER and DB_PASSWORD.");
            }
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found. Check pom.xml.", e);
        }

        return DriverManager.getConnection(url, user, password);
    }
}
