INSERT INTO industry (id, name, created_at)
SELECT 7101,'기타',NOW() WHERE NOT EXISTS(SELECT 1 FROM industry WHERE name='기타');

INSERT INTO industry (id, name, created_at)
SELECT 7102,'서비스업',NOW() WHERE NOT EXISTS(SELECT 1 FROM industry WHERE name='서비스업');

INSERT INTO industry (id, name, created_at)
SELECT 7103,'교육',NOW() WHERE NOT EXISTS(SELECT 1 FROM industry WHERE name='교육');

INSERT INTO industry (id, name, created_at)
SELECT 7104,'부동산',NOW() WHERE NOT EXISTS(SELECT 1 FROM industry WHERE name='부동산');

INSERT INTO industry (id, name, created_at)
SELECT 7105,'건설업',NOW() WHERE NOT EXISTS(SELECT 1 FROM industry WHERE name='건설업');

INSERT INTO industry (id, name, created_at)
SELECT 7106,'소매업',NOW() WHERE NOT EXISTS(SELECT 1 FROM industry WHERE name='소매업');

INSERT INTO industry (id, name, created_at)
SELECT 7107,'공연/문화',NOW() WHERE NOT EXISTS(SELECT 1 FROM industry WHERE name='공연/문화');

INSERT INTO industry (id, name, created_at)
SELECT 7108,'제조업',NOW() WHERE NOT EXISTS(SELECT 1 FROM industry WHERE name='제조업');

INSERT INTO industry (id, name, created_at)
SELECT 7109,'금융',NOW() WHERE NOT EXISTS(SELECT 1 FROM industry WHERE name='금융');

INSERT INTO industry (id, name, created_at)
SELECT 7110,'IT/소프트웨어',NOW() WHERE NOT EXISTS(SELECT 1 FROM industry WHERE name='IT/소프트웨어');
