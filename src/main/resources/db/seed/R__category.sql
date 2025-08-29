-- 1차
INSERT INTO category (id, name, parent_id, created_at)
SELECT 201, '회원정보/회원혜택', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=201);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 202, '기타', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=202);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9001, '서비스이용', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9001);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9002, '리포팅', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9002);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9003, '예약', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9003);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9004, '법적고지', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9004);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9005, '구매', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9005);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9006, '배송', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9006);

-- 2차
INSERT INTO category (id, name, parent_id, created_at)
SELECT 9101, '이용안내/공지', 9001, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9101);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9102, '피드백 요청', 9001, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9102);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9103, '방문서비스', 9001, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9103);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9104, '이용도구', 9001, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9104);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9201, '피드백', 9002, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9201);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9202, '요금청구', 9002, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9202);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9301, '예약완료', 9003, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9301);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9401, '내부 업무 알림', 9004, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9401);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9402, '주문/예약', 9004, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9402);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9501, '상품가입', 9005, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9501);

INSERT INTO category (id, name, parent_id, created_at)
SELECT 9601, '배송상태', 9006, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=9601);
