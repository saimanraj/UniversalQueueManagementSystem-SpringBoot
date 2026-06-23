-- ==========================================
-- UQS - Online Queue Management System
-- Database Schema
-- ==========================================

CREATE DATABASE IF NOT EXISTS uqs_db;
USE uqs_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(15),
    password VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'VENDOR', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Vendors Table
CREATE TABLE IF NOT EXISTS vendors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    shop_name VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    description TEXT,
    approved BOOLEAN DEFAULT FALSE,
    avg_service_time INT DEFAULT 5,
    address VARCHAR(300),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_approved (approved)
);

-- Queues Table
CREATE TABLE IF NOT EXISTS queues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id BIGINT NOT NULL UNIQUE,
    current_token INT DEFAULT 0,
    is_active BOOLEAN DEFAULT FALSE,
    is_paused BOOLEAN DEFAULT FALSE,
    opened_at TIMESTAMP NULL,
    last_called_at TIMESTAMP NULL,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
);

-- Tokens Table
CREATE TABLE IF NOT EXISTS tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,
    token_no INT NOT NULL,
    status ENUM('WAITING', 'SERVING', 'SERVED', 'CANCELLED') DEFAULT 'WAITING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    served_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE,
    INDEX idx_vendor_status (vendor_id, status),
    INDEX idx_user_vendor (user_id, vendor_id)
);

-- Insert default Admin user (password: admin123)
-- Hash generated and verified with BCrypt strength 10
INSERT IGNORE INTO users (name, email, phone, password, role)
VALUES ('Admin', 'admin@uqs.com', '9999999999',
        '$2a$10$FSivM0QCmoH8eAu22pLFJOvaYhV2W/MoPpSnb1xoxdKvpk/zeUUYe', 'ADMIN');
