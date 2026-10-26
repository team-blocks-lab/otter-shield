-- MySQL 8.0.46 기준 / 현재 합의된 OTT 설계 기반, 앱 매핑은 구현 시 대조
CREATE DATABASE IF NOT EXISTS ott CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ott;
CREATE TABLE IF NOT EXISTS member (
 member_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(50) NOT NULL,
 login_id VARCHAR(50) NOT NULL,
 password_hash VARCHAR(255) NOT NULL,
 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 CONSTRAINT uk_member_login_id UNIQUE(login_id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS video (
 video_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 title VARCHAR(200) NOT NULL,
 thumbnail_key VARCHAR(500) NOT NULL,
 genre VARCHAR(50) NULL,
 summary TEXT NOT NULL,
 actor VARCHAR(255) NULL,
 video_key VARCHAR(500) NOT NULL,
 duration_seconds INT NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 CONSTRAINT chk_video_status CHECK(status IN ('PUBLIC','PRIVATE')),
 CONSTRAINT chk_video_duration CHECK(duration_seconds>0),
 INDEX idx_video_list(status,created_at,video_id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS subtitle (
 subtitle_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 video_id BIGINT NOT NULL,
 language VARCHAR(2) NOT NULL,
 subtitle_key VARCHAR(500) NOT NULL,
 CONSTRAINT fk_subtitle_video FOREIGN KEY(video_id) REFERENCES video(video_id),
 CONSTRAINT uk_subtitle_language UNIQUE(video_id,language),
 CONSTRAINT chk_subtitle_language CHECK(language IN ('ko','en'))
) ENGINE=InnoDB;
