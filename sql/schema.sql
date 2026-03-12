-- ============================================
-- Banking Transaction Manager - Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS banking_app;
USE banking_app;

-- Users table
CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Accounts table
CREATE TABLE accounts (
    account_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    account_number  VARCHAR(20) NOT NULL UNIQUE,
    account_type    ENUM('SAVINGS', 'CURRENT') DEFAULT 'SAVINGS',
    balance         DECIMAL(15, 2) DEFAULT 0.00,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Transactions table
CREATE TABLE transactions (
    transaction_id      INT AUTO_INCREMENT PRIMARY KEY,
    account_id          INT NOT NULL,
    transaction_type    ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,
    amount              DECIMAL(15, 2) NOT NULL,
    balance_after       DECIMAL(15, 2) NOT NULL,
    description         VARCHAR(255),
    transaction_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Sample data
INSERT INTO users (full_name, email, password) VALUES
('Alice Johnson', 'alice@email.com', 'hashed_password_1'),
('Bob Smith',     'bob@email.com',   'hashed_password_2');

INSERT INTO accounts (user_id, account_number, account_type, balance) VALUES
(1, 'ACC0000000001', 'SAVINGS', 5000.00),
(2, 'ACC0000000002', 'CURRENT', 12000.00);