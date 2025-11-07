package com.example.dao;

import com.example.models.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    // Fetch all suppliers
    public static List<Supplier> getAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Supplier supplier = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("email"),
                        rs.getString("address")
                );
                suppliers.add(supplier);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching suppliers: " + e.getMessage());
            e.printStackTrace();
        }

        return suppliers;
    }

    // Add a new supplier and return the generated ID
    public static int add(Supplier supplier) {
        String sql = "INSERT INTO suppliers (name, contact, email, address) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContact());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getAddress());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        System.out.println("✅ Supplier added with ID: " + id);
                        return id;
                    }
                }
            } else {
                System.err.println("⚠️ Supplier insertion failed.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error adding supplier: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }
}
