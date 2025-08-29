CREATE TABLE user (
                      id BIGINT NOT NULL,
                      email VARCHAR(255) NOT NULL,
                      password_hash VARCHAR(255) NOT NULL,
                      name VARCHAR(100) NULL,
                      status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                      locked TINYINT NOT NULL,
                      locked_at DATETIME NULL,
                      last_login_at DATETIME NULL,
                      created_at DATETIME NOT NULL,
                      updated_at DATETIME NOT NULL,
                      CONSTRAINT PK_USER PRIMARY KEY (id)
);

CREATE TABLE role (
                      role_id BIGINT NOT NULL,
                      role_name VARCHAR(50) NOT NULL,
                      CONSTRAINT PK_ROLE PRIMARY KEY (role_id)
);

CREATE TABLE user_role_mapping (
                                   user_id BIGINT NOT NULL,
                                   role_id BIGINT NOT NULL,
                                   CONSTRAINT PK_USER_ROLE_MAPPING PRIMARY KEY (user_id, role_id)
);

CREATE TABLE refresh_tokens (
                                id BIGINT NOT NULL,
                                user_id BIGINT NOT NULL,
                                refresh_token CHAR(128) NOT NULL,
                                issued_at DATETIME NOT NULL,
                                expires_at DATETIME NOT NULL,
                                revoked_at DATETIME NULL,
                                client_info VARCHAR(255) NULL,
                                CONSTRAINT PK_REFRESH_TOKENS PRIMARY KEY (id)
);

CREATE TABLE password_reset_token (
                                      id BIGINT NOT NULL,
                                      user_id BIGINT NOT NULL,
                                      token CHAR(64) NOT NULL,
                                      expires_at DATETIME NOT NULL,
                                      used_at DATETIME NULL,
                                      requested_ip VARCHAR(45) NULL,
                                      created_at DATETIME NOT NULL,
                                      CONSTRAINT PK_PASSWORD_RESET_TOKEN PRIMARY KEY (id)
);

CREATE TABLE email_verification_token (
                                          id BIGINT NOT NULL,
                                          user_id BIGINT NOT NULL,
                                          token CHAR(64) NOT NULL,
                                          expires_at DATETIME NOT NULL,
                                          used_at DATETIME NULL,
                                          created_at DATETIME NOT NULL,
                                          extra_field VARCHAR(255) NULL,
                                          CONSTRAINT PK_EMAIL_VERIFICATION_TOKEN PRIMARY KEY (id)
);

CREATE TABLE audit_log (
                           id BIGINT NULL,
                           actor_user_id BIGINT NULL,
                           action VARCHAR(50) NOT NULL,
                           entity VARCHAR(50) NOT NULL,
                           entity_id BIGINT NOT NULL,
                           payload TEXT NULL,
                           created_at DATETIME NOT NULL
);



CREATE TABLE template (
                          id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          category_id BIGINT NOT NULL,
                          title VARCHAR(255) NULL,
                          content TEXT NULL,
                          request_content TEXT NULL,
                          status ENUM('CREATE_REQUESTED','CREATED','APPROVE_REQUESTED','APPROVED','REJECTED','FAILED','DELETED') NULL,
                          type ENUM('LINK','MESSAGE','DOCUMENT') NULL,
                          is_public BOOLEAN NULL,
                          image_url VARCHAR(500) NULL,
                          reject_reason VARCHAR(500) NULL,
                          reject_reason_summary VARCHAR(500) NULL,
                          created_at TIMESTAMP NULL,
                          updated_at TIMESTAMP NULL,
                          CONSTRAINT PK_TEMPLATE PRIMARY KEY (id)
);

CREATE TABLE template_variable (
                                   id BIGINT NOT NULL,
                                   template_id BIGINT NOT NULL,
                                   variable_key VARCHAR(100) NULL,
                                   placeholder VARCHAR(100) NULL,
                                   input_type VARCHAR(50) NULL,
                                   created_at TIMESTAMP NULL,
                                   CONSTRAINT PK_TEMPLATE_VARIABLE PRIMARY KEY (id)
);

CREATE TABLE template_button (
                                 id BIGINT NOT NULL,
                                 template_id BIGINT NOT NULL,
                                 name VARCHAR(100) NULL,
                                 ordering INT NULL,
                                 link_pc VARCHAR(500) NULL,
                                 link_and VARCHAR(500) NULL,
                                 link_ios VARCHAR(500) NULL,
                                 created_at TIMESTAMP NULL,
                                 CONSTRAINT PK_TEMPLATE_BUTTON PRIMARY KEY (id)
);

CREATE TABLE template_history (
                                  id BIGINT NOT NULL,
                                  template_id BIGINT NOT NULL,
                                  status ENUM('CREATE_REQUESTED','CREATED','APPROVE_REQUESTED','APPROVED','REJECTED','FAILED','DELETED') NULL,
                                  created_at TIMESTAMP NULL,
                                  CONSTRAINT PK_TEMPLATE_HISTORY PRIMARY KEY (id)
);



CREATE TABLE category (
                          id BIGINT NOT NULL,
                          name VARCHAR(100) NULL,
                          parent_id BIGINT NULL,
                          created_at TIMESTAMP NULL,
                          CONSTRAINT PK_CATEGORY PRIMARY KEY (id)
);

CREATE TABLE industry (
                          id BIGINT NOT NULL,
                          name VARCHAR(100) NULL,
                          created_at TIMESTAMP NULL,
                          CONSTRAINT PK_INDUSTRY PRIMARY KEY (id)
);

CREATE TABLE purpose (
                         id BIGINT NOT NULL,
                         name VARCHAR(100) NULL,
                         created_at TIMESTAMP NULL,
                         CONSTRAINT PK_PURPOSE PRIMARY KEY (id)
);

CREATE TABLE template_industry (
                                   id BIGINT NOT NULL,
                                   industry_id BIGINT NOT NULL,
                                   template_id BIGINT NOT NULL,
                                   created_at TIMESTAMP NULL,
                                   CONSTRAINT PK_TEMPLATE_INDUSTRY PRIMARY KEY (id)
);

CREATE TABLE template_purpose (
                                  id BIGINT NOT NULL,
                                  purpose_id BIGINT NOT NULL,
                                  template_id BIGINT NOT NULL,
                                  created_at TIMESTAMP NULL,
                                  CONSTRAINT PK_TEMPLATE_PURPOSE PRIMARY KEY (id)
);
