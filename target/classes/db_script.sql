-- Database: medicine_tracker

CREATE DATABASE IF NOT EXISTS medicine_tracker;
USE medicine_tracker;

CREATE TABLE IF NOT EXISTS suppliers (
  supplier_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  contact VARCHAR(50),
  email VARCHAR(100),
  address TEXT
);

CREATE TABLE IF NOT EXISTS medicines (
  medicine_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  batch_no VARCHAR(80) NOT NULL,
  expiry_date DATE NOT NULL,
  quantity INT DEFAULT 0,
  min_quantity INT DEFAULT 0,
  supplier_id INT,
  barcode VARCHAR(150),
  FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS alerts (
  alert_id INT AUTO_INCREMENT PRIMARY KEY,
  message TEXT NOT NULL,
  type ENUM('expiry','stock') NOT NULL,
  date_created DATETIME DEFAULT CURRENT_TIMESTAMP
);
