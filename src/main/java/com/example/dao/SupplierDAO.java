package com.example.dao;

import com.example.models.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierDAO {

    // Fetch all suppliers
    public static List<Supplier> getAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching suppliers: " + e.getMessage());
            e.printStackTrace();
        }

        return suppliers;
    }

    // Fetch a supplier by ID
    public static Optional<Supplier> getById(int id) {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSupplier(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching supplier by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
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

    // Update existing supplier
    public static boolean update(Supplier supplier) {
        String sql = "UPDATE suppliers SET name=?, contact=?, email=?, address=? WHERE supplier_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContact());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getAddress());
            ps.setInt(5, supplier.getSupplierId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating supplier: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Delete supplier by ID
    public static boolean delete(int supplierId) {
        String sql = "DELETE FROM suppliers WHERE supplier_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting supplier: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Utility method to map ResultSet to Supplier object
    private static Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        return new Supplier(
                rs.getInt("supplier_id"),
                rs.getString("name"),
                rs.getString("contact"),
                rs.getString("email"),
                rs.getString("address")
        );
    }
}
