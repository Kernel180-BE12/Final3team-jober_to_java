INSERT INTO purpose (id, name, created_at)
SELECT 8501,'공지/안내',NOW() WHERE NOT EXISTS(SELECT 1 FROM purpose WHERE name='공지/안내');

INSERT INTO purpose (id, name, created_at)
SELECT 8502,'신청',NOW() WHERE NOT EXISTS(SELECT 1 FROM purpose WHERE name='신청');

INSERT INTO purpose (id, name, created_at)
SELECT 8503,'예약',NOW() WHERE NOT EXISTS(SELECT 1 FROM purpose WHERE name='예약');
