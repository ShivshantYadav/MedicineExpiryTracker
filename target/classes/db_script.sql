-- ==========================================
-- DATABASE: Medicine Expiry Tracker
-- ==========================================
CREATE DATABASE IF NOT EXISTS medicine_tracker;
USE medicine_tracker;

-- ==========================================
-- 1️⃣  SUPPLIERS TABLE
-- ==========================================
CREATE TABLE IF NOT EXISTS suppliers (
  supplier_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  contact VARCHAR(50),
  email VARCHAR(100),
  address TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ==========================================
-- 2️⃣  MEDICINES TABLE
-- ==========================================
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

-- Optional indexes for performance
CREATE INDEX idx_medicine_expiry ON medicines(expiry_date);
CREATE INDEX idx_medicine_name ON medicines(name);
CREATE INDEX idx_medicine_status ON medicines(status);

-- ==========================================
-- 3️⃣  ALERTS TABLE
-- ==========================================
CREATE TABLE IF NOT EXISTS alerts (
  alert_id INT AUTO_INCREMENT PRIMARY KEY,
  message VARCHAR(255) NOT NULL,
  type ENUM('expiry', 'stock') NOT NULL,
  related_medicine_id INT NULL,
  date_created DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (related_medicine_id) REFERENCES medicines(medicine_id)
      ON DELETE CASCADE
);

-- Optional indexes
CREATE INDEX idx_alert_type ON alerts(type);
CREATE INDEX idx_alert_date ON alerts(date_created);

-- ==========================================
-- ✅ Optional Sample Data
-- ==========================================
INSERT INTO suppliers (name, contact, email, address) VALUES
('Apollo Distributors', '9876543210', 'apollo@supply.com', 'Mumbai'),
('HealthPlus Pharma', '9123456789', 'info@healthplus.com', 'Pune');

INSERT INTO medicines (name, batch_no, expiry_date, quantity, min_quantity, supplier_id, barcode)
VALUES
('Paracetamol 500mg', 'P500-2025', '2025-12-15', 150, 20, 1, 'PARA5002025'),
('Cetrizine 10mg', 'C10-2025', '2025-11-30', 80, 10, 2, 'CET102025');
