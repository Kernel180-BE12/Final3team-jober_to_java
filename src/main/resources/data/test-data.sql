INSERT INTO industry (id, name) VALUES (1, '학원') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO industry (id, name) VALUES (4, '공연/행사') ON DUPLICATE KEY UPDATE name = name;

INSERT INTO purpose (id, name) VALUES (2, '공지/안내') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO purpose (id, name) VALUES (8, '예약') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO purpose (id, name) VALUES (11, '기타') ON DUPLICATE KEY UPDATE name = name;
