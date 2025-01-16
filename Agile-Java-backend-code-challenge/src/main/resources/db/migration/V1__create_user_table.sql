CREATE DATABASE IF NOT EXISTS user_management;
USE user_management;

CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100),
    email VARCHAR(100),
    gender VARCHAR(10),
    picture VARCHAR(255),
    country VARCHAR(50),
    state VARCHAR(50),
    city VARCHAR(50)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;