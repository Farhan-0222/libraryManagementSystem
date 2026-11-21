package com.library.dao;

import com.library.util.DBConnection;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Reads and writes the settings table (fine rate, loan days, max books, library name). */
public class SettingsDAO {

    public String get(String key, String defaultValue) {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public double getDouble(String key, double def) {
        try { return Double.parseDouble(get(key, String.valueOf(def))); } catch (NumberFormatException e) { return def; }
    }

    public int getInt(String key, int def) {
        try { return Integer.parseInt(get(key, String.valueOf(def))); } catch (NumberFormatException e) { return def; }
    }

    /** Insert or update one setting. */
    public void set(String key, String value) throws SQLException {
        String sql = "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?) "
                   + "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        }
    }

    public Map<String, String> getAll() {
        Map<String, String> map = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT setting_key, setting_value FROM settings")) {
            while (rs.next()) map.put(rs.getString(1), rs.getString(2));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
