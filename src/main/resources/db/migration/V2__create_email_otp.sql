-- V2__create_email_otp.sql

-- 1) email_otp 신규 테이블 (이미 있으면 패스)
CREATE TABLE IF NOT EXISTS email_otp (
                                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                         email VARCHAR(255) NOT NULL,
    code_hash VARCHAR(255) NOT NULL,
    expires_at DATETIME NOT NULL,
    verified BIT(1) NOT NULL DEFAULT 0,
    attempt_count INT NOT NULL DEFAULT 0,
    last_sent_at DATETIME NOT NULL,
    resend_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email_otp_email (email),
    INDEX idx_email_otp_expires (expires_at),
    INDEX idx_email_otp_pending (email, verified, expires_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) email_verification_token 컬럼 보강 (IF NOT EXISTS 미사용: 동적 SQL로 가드)

-- 2-1) created_at 없으면 추가
SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'email_verification_token'
    AND COLUMN_NAME = 'created_at'
);
SET @sql := IF(@col_exists = 0,
  'ALTER TABLE email_verification_token ADD COLUMN created_at DATETIME NULL',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-2) updated_at 없으면 추가
SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'email_verification_token'
    AND COLUMN_NAME = 'updated_at'
);
SET @sql := IF(@col_exists = 0,
  'ALTER TABLE email_verification_token ADD COLUMN updated_at DATETIME NULL',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-3) 타임스탬프 디폴트/ON UPDATE 표준화
ALTER TABLE email_verification_token
    MODIFY COLUMN created_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE email_verification_token
    MODIFY COLUMN updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 3) pre-signup 지원 확장

-- 3-1) user_id NULL 허용
ALTER TABLE email_verification_token
    MODIFY COLUMN user_id BIGINT NULL;

-- 3-2) email / pre_signup (없으면 추가)
-- email
SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'email_verification_token'
    AND COLUMN_NAME = 'email'
);
SET @sql := IF(@col_exists = 0,
  'ALTER TABLE email_verification_token ADD COLUMN email VARCHAR(255) NULL',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- pre_signup
SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'email_verification_token'
    AND COLUMN_NAME = 'pre_signup'
);
SET @sql := IF(@col_exists = 0,
  'ALTER TABLE email_verification_token ADD COLUMN pre_signup BIT(1) NOT NULL DEFAULT 1',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3-3) 인덱스들 (없으면 생성)
-- idx_evt_email
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'email_verification_token'
    AND INDEX_NAME = 'idx_evt_email'
);
SET @sql := IF(@idx_exists = 0,
  'CREATE INDEX idx_evt_email ON email_verification_token (email)',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- idx_evt_exp
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'email_verification_token'
    AND INDEX_NAME = 'idx_evt_exp'
);
SET @sql := IF(@idx_exists = 0,
  'CREATE INDEX idx_evt_exp ON email_verification_token (expires_at)',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- uq_evt_token (토큰 유니크)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'email_verification_token'
    AND INDEX_NAME = 'uq_evt_token'
);
SET @sql := IF(@idx_exists = 0,
  'CREATE UNIQUE INDEX uq_evt_token ON email_verification_token (token)',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
