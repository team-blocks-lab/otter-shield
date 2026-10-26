-- MySQL 8.0.46 기준 / 기존 데이터 삭제 없음
CREATE DATABASE IF NOT EXISTS intranet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE intranet;
CREATE TABLE IF NOT EXISTS employee (
 employee_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 employee_number VARCHAR(30) NOT NULL,
 password_hash VARCHAR(255) NOT NULL,
 nickname VARCHAR(30) NULL,
 first_login BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT uk_employee_number UNIQUE(employee_number)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS post (
 post_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 employee_id BIGINT NOT NULL,
 category VARCHAR(20) NOT NULL,
 title VARCHAR(200) NOT NULL,
 content TEXT NOT NULL,
 anonymous BOOLEAN NOT NULL DEFAULT FALSE,
 image_key VARCHAR(500) NULL,
 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 CONSTRAINT fk_post_employee FOREIGN KEY(employee_id) REFERENCES employee(employee_id),
 CONSTRAINT chk_post_category CHECK(category IN ('PRAISE','LUNCH')),
 CONSTRAINT chk_post_anonymous CHECK(category='PRAISE' OR anonymous=FALSE),
 INDEX idx_post_feed(created_at,post_id)
) ENGINE=InnoDB;
