SET @db := DATABASE();

/* =======================
   0) Legacy cleanup
   ======================= */
-- 평문 refresh_token 컬럼이 있으면 제거
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND COLUMN_NAME='refresh_token'
);
SET @sql := IF(@exists=1,
  'ALTER TABLE refresh_tokens DROP COLUMN refresh_token',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


/* =======================
   1) Columns (ensure shape)
   ======================= */

-- token_hash: VARCHAR(64) NOT NULL (Hibernate 기대 타입)
SET @dt := NULL; SET @len := NULL;
SELECT DATA_TYPE, CHARACTER_MAXIMUM_LENGTH
INTO @dt, @len
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND COLUMN_NAME='token_hash'
    LIMIT 1;
SET @sql := IF(
  @dt IS NULL,
  'ALTER TABLE refresh_tokens ADD COLUMN token_hash VARCHAR(64) NOT NULL COMMENT ''SHA-256(pepper) of raw refresh token''',
  IF(LOWER(@dt)='varchar' AND IFNULL(@len,0)=64,
     'DO 0',
     'ALTER TABLE refresh_tokens MODIFY COLUMN token_hash VARCHAR(64) NOT NULL COMMENT ''SHA-256(pepper) of raw refresh token'''
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- jti: VARCHAR(64) NULL (없으면 추가)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND COLUMN_NAME='jti'
);
SET @sql := IF(@exists=0,
  'ALTER TABLE refresh_tokens ADD COLUMN jti VARCHAR(64) NULL COMMENT ''JWT ID of this refresh token''',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- replaced_by_jti: VARCHAR(64) NULL (없으면 추가)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND COLUMN_NAME='replaced_by_jti'
);
SET @sql := IF(@exists=0,
  'ALTER TABLE refresh_tokens ADD COLUMN replaced_by_jti VARCHAR(64) NULL COMMENT ''JTI that replaced this token''',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- revoked: TINYINT(1) NOT NULL DEFAULT 0 (없으면 추가)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND COLUMN_NAME='revoked'
);
SET @sql := IF(@exists=0,
  'ALTER TABLE refresh_tokens ADD COLUMN revoked TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''1=revoked''',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- rotated_at: DATETIME NULL (없으면 추가)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND COLUMN_NAME='rotated_at'
);
SET @sql := IF(@exists=0,
  'ALTER TABLE refresh_tokens ADD COLUMN rotated_at DATETIME NULL COMMENT ''Rotation/replacement time''',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- expires_at: DATETIME NOT NULL (없으면 추가)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND COLUMN_NAME='expires_at'
);
SET @sql := IF(@exists=0,
  'ALTER TABLE refresh_tokens ADD COLUMN expires_at DATETIME NOT NULL COMMENT ''Refresh token expiry''',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- issued_at: DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP (DB auto; 없으면 추가, 있으면 보장)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND COLUMN_NAME='issued_at'
);
SET @sql := IF(@exists=0,
  'ALTER TABLE refresh_tokens ADD COLUMN issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''Issued time (DB managed)''',
  'ALTER TABLE refresh_tokens MODIFY COLUMN issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''Issued time (DB managed)'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- created_at: DATETIME NULL DEFAULT CURRENT_TIMESTAMP (없으면 추가)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND COLUMN_NAME='created_at'
);
SET @sql := IF(@exists=0,
  'ALTER TABLE refresh_tokens ADD COLUMN created_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''Row creation time (DB managed)''',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- updated_at: DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP (없으면 추가)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND COLUMN_NAME='updated_at'
);
SET @sql := IF(@exists=0,
  'ALTER TABLE refresh_tokens ADD COLUMN updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''Row update time (DB managed)''',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


/* =======================
   2) Indexes / Constraints
   ======================= */

-- (선택) token_hash HEX 체크(64자리 소문자) - MySQL 8.0.16+
SET @has_chk := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens'
    AND CONSTRAINT_TYPE='CHECK' AND CONSTRAINT_NAME='chk_token_hash_hex'
);
SET @sql := IF(@has_chk=0,
  'ALTER TABLE refresh_tokens ADD CONSTRAINT chk_token_hash_hex CHECK (token_hash REGEXP ''^[0-9a-f]{64}$'')',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- UNIQUE(token_hash)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND INDEX_NAME='uq_refresh_tokens_token_hash'
);
SET @sql := IF(@exists=0,
  'CREATE UNIQUE INDEX uq_refresh_tokens_token_hash ON refresh_tokens (token_hash)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- INDEX(jti)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND INDEX_NAME='idx_refresh_tokens_jti'
);
SET @sql := IF(@exists=0,
  'CREATE INDEX idx_refresh_tokens_jti ON refresh_tokens (jti)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- INDEX(replaced_by_jti)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND INDEX_NAME='idx_refresh_tokens_replaced_by'
);
SET @sql := IF(@exists=0,
  'CREATE INDEX idx_refresh_tokens_replaced_by ON refresh_tokens (replaced_by_jti)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- INDEX(revoked)
SET @exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='refresh_tokens' AND INDEX_NAME='idx_refresh_tokens_revoked'
);
SET @sql := IF(@exists=0,
  'CREATE INDEX idx_refresh_tokens_revoked ON refresh_tokens (revoked)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
