package com.example.dao;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Automatically creates required tables for MedicineExpiryTracker
 * if they don't exist in the connected MySQL database.
 */
public class DBInitializer {

    public static void initialize() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1️⃣ SUPPLIERS TABLE
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS suppliers (
                    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    contact VARCHAR(50),
                    email VARCHAR(100),
                    address TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                );
            """);

            // 2️⃣ MEDICINES TABLE
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS medicines (
                    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(150) NOT NULL,
                    batch_no VARCHAR(80) NOT NULL UNIQUE,
                    expiry_date DATE NOT NULL,
                    quantity INT DEFAULT 0 CHECK (quantity >= 0),
                    min_quantity INT DEFAULT 0 CHECK (min_quantity >= 0),
                    supplier_id INT,
                    barcode VARCHAR(150) UNIQUE,
                    status ENUM('active', 'expired', 'low_stock') DEFAULT 'active',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
                        ON DELETE SET NULL
                        ON UPDATE CASCADE
                );
            """);

            // 3️⃣ ALERTS TABLE
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS alerts (
                    alert_id INT AUTO_INCREMENT PRIMARY KEY,
                    message VARCHAR(255) NOT NULL,
                    type ENUM('expiry', 'stock') NOT NULL,
                    related_medicine_id INT NULL,
                    date_created DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (related_medicine_id) REFERENCES medicines(medicine_id)
                        ON DELETE CASCADE
                );
            """);

            System.out.println("✅ Database tables verified/created successfully.");

        } catch (Exception e) {
            System.err.println("⚠️ Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
