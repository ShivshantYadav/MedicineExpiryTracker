package com.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles database connections for the MedicineExpiryTracker application.
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/medicine_tracker";
    private static final String USER = "root";        // 👈 Change if your MySQL username is different
    private static final String PASSWORD = "";        // 👈 Add your MySQL password here if set

    private static Connection connection = null;

    /**
     * Returns a singleton database connection instance.
     * 
     * @return Connection object to MySQL database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connected to database successfully!");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found!", e);
            }
        }
        return connection;
    }

    /**
     * Closes the database connection safely.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error closing connection: " + e.getMessage());
        }
    }
}
