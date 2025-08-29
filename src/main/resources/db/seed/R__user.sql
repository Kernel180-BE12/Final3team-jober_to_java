INSERT INTO `user` (id, email, password_hash, name, status, locked, locked_at, last_login_at, created_at, updated_at)
VALUES
    (101, 'shingu@gmi.com', 'pw', '신구', 'ACTIVE', 0, NULL, NULL, NOW(), NOW()),
    (102, 'neo@allwork.com', 'pw', '네오', 'ACTIVE', 0, NULL, NULL, NOW(), NOW()),
    (1, 'neymar@viabarca.com', 'hash1', '네이마르', 'ACTIVE', 0, NULL, '2025-01-01 09:00:00', '2025-01-01 08:59:00', '2025-01-01 09:00:00'),
    (2, 'rodrigo@halamadrid.com', 'hash2', '호드리구', 'PENDING', 0, NULL, NULL, '2025-01-02 09:00:00', '2025-01-02 09:00:00'),
    (3, 'lamin@viabarca.com', 'hash3', '라민야말', 'ACTIVE', 0, NULL, '2025-01-03 10:00:00', '2025-01-03 09:59:00', '2025-01-03 10:00:00'),
    (4, 'frengky@viabarca.com', 'hash4', '더용', 'INACTIVE', 1, '2025-01-04 09:00:00', NULL, '2025-01-04 09:00:00', '2025-01-04 09:00:00'),
    (5, 'jude@halamadrid.com', 'hash5', '벨링엄', 'ACTIVE', 0, NULL, '2025-01-05 09:00:00', '2025-01-05 09:00:00', '2025-01-05 09:00:00'),
    (6, 'cristiana@halamadrid.com', 'hash6', '호날두', 'ACTIVE', 0, NULL, NULL, '2025-01-06 09:00:00', '2025-01-06 09:00:00'),
    (7, 'messi@viabarca.com', 'hash7', '메시', 'PENDING', 0, NULL, NULL, '2025-01-07 09:00:00', '2025-01-07 09:00:00');
