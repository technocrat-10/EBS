-- Create the database
CREATE DATABASE IF NOT EXISTS ebs;
USE ebs;

-- Create customer table
CREATE TABLE customer (
    meter_no VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100),
    address VARCHAR(200),
    city VARCHAR(50),
    state VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20)
);

-- Create bill table
CREATE TABLE bill (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    meter_no VARCHAR(20),
    month VARCHAR(20),
    year INT,
    units INT,
    total_bill DECIMAL(10,2),
    status VARCHAR(20),
    FOREIGN KEY (meter_no) REFERENCES customer(meter_no)
);

-- Create user table for login
CREATE TABLE user (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50),
    role VARCHAR(20)
);

-- Insert default admin user
INSERT INTO user (username, password, role) VALUES ('admin', 'admin', 'admin'); 