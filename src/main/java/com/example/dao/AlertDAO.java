package com.example.dao;

import com.example.models.AlertModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {

    // ✅ Add a new alert
    public static void addAlert(String message, String type) throws DAOException {
        final String sql = "INSERT INTO alerts (message, type, date_created) VALUES (?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, message);
            stmt.setString(2, type);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DAOException("No alert inserted for: " + message, null);
            } else {
                System.out.println("✅ Alert added: " + message + " (" + type + ")");
            }

        } catch (SQLException e) {
            throw new DAOException("Error adding alert: " + message, e);
        }
    }

    // ✅ Fetch all alerts
    public static List<AlertModel> getAll() throws DAOException {
        List<AlertModel> alerts = new ArrayList<>();
        final String sql = "SELECT alert_id, message, type, date_created FROM alerts ORDER BY date_created DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("date_created");
                AlertModel alert = new AlertModel(
                        rs.getInt("alert_id"),
                        rs.getString("message"),
                        rs.getString("type"),
                        ts != null ? ts.toLocalDateTime() : null);
                alerts.add(alert);
            }

        } catch (SQLException e) {
            throw new DAOException("Error fetching alerts", e);
        }

        return alerts;
    }

    // ✅ Clear all alerts
    public static void clearAll() throws DAOException {
        final String sql = "DELETE FROM alerts";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int deleted = stmt.executeUpdate();
            System.out.println("🗑️ Cleared " + deleted + " alerts from database.");

        } catch (SQLException e) {
            throw new DAOException("Error clearing alerts", e);
        }
    }
}
