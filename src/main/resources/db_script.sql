-- Database: medicine_tracker

CREATE DATABASE IF NOT EXISTS medicine_tracker;
USE medicine_tracker;

-- ===============================
-- 1️⃣  SUPPLIERS TABLE
-- ===============================
CREATE TABLE IF NOT EXISTS suppliers (
  supplier_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  contact VARCHAR(50),
  email VARCHAR(100),
  address TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- 2️⃣  MEDICINES TABLE
-- ===============================
CREATE TABLE IF NOT EXISTS medicines (
  medicine_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  batch_no VARCHAR(80) NOT NULL UNIQUE,
  expiry_date DATE NOT NULL,
  quantity INT DEFAULT 0 CHECK (quantity >= 0),
  min_quantity INT DEFAULT 0 CHECK (min_quantity >= 0),
  supplier_id INT,
  barcode VARCHAR(150) UNIQUE,
  FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Optional: For faster searches by expiry or name
CREATE INDEX idx_medicine_expiry ON medicines(expiry_date);
CREATE INDEX idx_medicine_name ON medicines(name);

-- ===============================
-- 3️⃣  ALERTS TABLE
-- ===============================
CREATE TABLE IF NOT EXISTS alerts (
  alert_id INT AUTO_INCREMENT PRIMARY KEY,
  message VARCHAR(255) NOT NULL,
  type ENUM('expiry', 'stock') NOT NULL,
  date_created DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Optional: Add index for sorting/filtering
CREATE INDEX idx_alert_type ON alerts(type);
CREATE INDEX idx_alert_date ON alerts(date_created);
