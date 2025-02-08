CREATE DATABASE IF NOT EXISTS DataDB;

USE DataDB;

CREATE TABLE IF NOT EXISTS logins_history (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    login_time  DATETIME DEFAULT CURRENT_TIMESTAMP,
    login_ip    VARCHAR(45) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS upload_history (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    file_name   VARCHAR(255) NOT NULL,
    file_type   VARCHAR(200) NOT NULL,
    file_data   LONGBLOB NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    firstName   VARCHAR(100) NOT NULL,
    lastName    VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(20) NOT NULL,
    password    VARCHAR(4000) NOT NULL,
    image       LONGBLOB NOT NULL
);
