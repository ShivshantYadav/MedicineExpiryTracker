package com.example.dao;

import com.example.models.Medicine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public static List<Medicine> getAll() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {

            while (rs.next()) {
                list.add(new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getString("batch_no"),
                        rs.getDate("expiry_date").toLocalDate(),
                        rs.getInt("quantity"),
                        rs.getInt("min_quantity"),
                        rs.getObject("supplier_id") == null ? null : rs.getInt("supplier_id"),
                        rs.getString("barcode")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean add(Medicine m) {
        String sql = "INSERT INTO medicines (name, batch_no, expiry_date, quantity, min_quantity, supplier_id, barcode) VALUES (?,?,?,?,?,?,?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, m.getName());
            p.setString(2, m.getBatchNo());
            p.setDate(3, Date.valueOf(m.getExpiryDate()));
            p.setInt(4, m.getQuantity());
            p.setInt(5, m.getMinQuantity());
            if (m.getSupplierId() == null) {
                p.setNull(6, java.sql.Types.INTEGER);
            } else {
                p.setInt(6, m.getSupplierId());
            }
            p.setString(7, m.getBarcode());

            return p.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateQuantity(int medicineId, int newQty) {
        String sql = "UPDATE medicines SET quantity = ? WHERE medicine_id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, newQty);
            p.setInt(2, medicineId);

            return p.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Medicine> getExpiringWithinDays(int days) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, days);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    list.add(new Medicine(
                            rs.getInt("medicine_id"),
                            rs.getString("name"),
                            rs.getString("batch_no"),
                            rs.getDate("expiry_date").toLocalDate(),
                            rs.getInt("quantity"),
                            rs.getInt("min_quantity"),
                            rs.getObject("supplier_id") == null ? null : rs.getInt("supplier_id"),
                            rs.getString("barcode")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<Medicine> getLowStock() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE quantity <= min_quantity";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {

            while (rs.next()) {
                list.add(new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getString("batch_no"),
                        rs.getDate("expiry_date").toLocalDate(),
                        rs.getInt("quantity"),
                        rs.getInt("min_quantity"),
                        rs.getObject("supplier_id") == null ? null : rs.getInt("supplier_id"),
                        rs.getString("barcode")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
