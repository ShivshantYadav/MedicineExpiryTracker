 package com.example.dao;

import com.example.models.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierDAO {

    // Fetch all suppliers
    public static List<Supplier> getAll() throws DAOException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Error fetching all suppliers", e);
        }

        return suppliers;
    }

    // Fetch supplier by ID
    public static Optional<Supplier> getById(int id) throws DAOException {
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
            throw new DAOException("Error fetching supplier by ID: " + id, e);
        }

        return Optional.empty();
    }

    // Add new supplier
    public static int add(Supplier supplier) throws DAOException {
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
                    if (rs.next()) return rs.getInt(1);
                }
            }
            throw new DAOException("Failed to add supplier: " + supplier.getName(), null);

        } catch (SQLException e) {
            throw new DAOException("Error adding supplier: " + supplier.getName(), e);
        }
    }

    // Update supplier
    public static void update(Supplier supplier) throws DAOException {
        String sql = "UPDATE suppliers SET name=?, contact=?, email=?, address=? WHERE supplier_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContact());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getAddress());
            ps.setInt(5, supplier.getSupplierId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DAOException("No supplier updated. ID may not exist: " + supplier.getSupplierId(), null);
            }

        } catch (SQLException e) {
            throw new DAOException("Error updating supplier ID: " + supplier.getSupplierId(), e);
        }
    }

    // ✅ Fixed Delete supplier
    public static void delete(int supplierId) throws DAOException {
        String sql = "DELETE FROM suppliers WHERE supplier_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId); // 🔧 Added this line
            int deleted = ps.executeUpdate();

            if (deleted == 0) {
                throw new DAOException("No supplier deleted. ID may not exist: " + supplierId, null);
            }

        } catch (SQLException e) {
            throw new DAOException("Error deleting supplier ID: " + supplierId, e);
        }
    }

    // Search suppliers by name
    public static List<Supplier> searchByName(String name) throws DAOException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers WHERE name LIKE ? ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapResultSetToSupplier(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error searching suppliers by name: " + name, e);
        }

        return suppliers;
    }

    // Map ResultSet to Supplier
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
