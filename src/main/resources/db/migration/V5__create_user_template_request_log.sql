CREATE TABLE user_template_request_failure_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_template_request_id BIGINT NULL,
    error_code VARCHAR(10) NULL,
    error_detail TEXT NULL,
    attempt_number INT NULL,
    user_agent VARCHAR(255) NULL,
    client_ip VARCHAR(64) NULL,
    http_status_code INT NULL,
    request_wait_time_ms INT NULL,
    response_time_ms INT NULL,
    request_received_at TIMESTAMP NULL,
    response_returned_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_template_request_failure_log
        FOREIGN KEY (user_template_request_id)
            REFERENCES user_template_request (id)
);
