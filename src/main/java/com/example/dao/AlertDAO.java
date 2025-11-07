package com.example.dao;

import com.example.models.AlertModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {

    // Insert a new alert into the database
    public static void addAlert(String message, String type) {
        String sql = "INSERT INTO alerts (message, type) VALUES (?, ?)";
        try (Connection c = DBConnection.getConnection();
            PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, message);
            p.setString(2, type);
            int rows = p.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Alert added successfully: " + message);
            } else {
                System.err.println("⚠️ No alert inserted (check database structure).");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error while adding alert: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Fetch all alerts from the database
    public static List<AlertModel> getAll() {
        List<AlertModel> list = new ArrayList<>();
        String sql = "SELECT * FROM alerts ORDER BY date_created DESC";

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("date_created");
                AlertModel a = new AlertModel(
                        rs.getInt("alert_id"),
                        rs.getString("message"),
                        rs.getString("type"),
                        ts != null ? ts.toLocalDateTime() : null
                );
                list.add(a);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error while fetching alerts: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }
}
