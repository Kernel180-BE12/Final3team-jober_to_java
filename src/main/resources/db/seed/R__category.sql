-- 1차: 상위 카테고리
INSERT INTO category (id, name, parent_id, created_at)
SELECT '001', '회원', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='001');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '002', '구매', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='002');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '003', '예약', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='003');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '004', '서비스이용', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='004');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '005', '리포팅', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='005');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '006', '배송', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='006');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '007', '법적고지', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='007');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '008', '업무알림', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='008');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '009', '쿠폰/포인트', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='009');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '999999', '기타', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='999999');

-- 2차: 하위 카테고리
-- '회원' 하위
INSERT INTO category (id, name, parent_id, created_at)
SELECT '001001', '회원가입', '001', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='001001');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '001002', '인증/비밀번호/로그인', '001', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='001002');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '001003', '회원정보/회원혜택', '001', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='001003');

-- '구매' 하위
INSERT INTO category (id, name, parent_id, created_at)
SELECT '002001', '구매완료', '002', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='002001');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '002002', '상품가입', '002', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='002002');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '002003', '진행상태', '002', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='002003');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '002004', '구매취소', '002', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='002004');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '002005', '구매예약/입고알림', '002', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='002005');

-- '예약' 하위
INSERT INTO category (id, name, parent_id, created_at)
SELECT '003001', '예약완료/예약내역', '003', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='003001');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '003002', '예약상태', '003', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='003002');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '003003', '예약취소', '003', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='003003');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '003004', '예약알림/리마인드', '003', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='003004');

-- '서비스이용' 하위
INSERT INTO category (id, name, parent_id, created_at)
SELECT '004001', '이용안내/공지', '004', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='004001');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '004002', '신청접수', '004', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='004002');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '004003', '처리완료', '004', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='004003');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '004004', '이용도구', '004', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='004004');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '004005', '방문서비스', '004', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='004005');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '004006', '피드백 요청', '004', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='004006');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '004007', '구매감사/이용확인', '004', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='004007');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '004008', '리마인드', '004', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='004008');

-- '리포팅' 하위
INSERT INTO category (id, name, parent_id, created_at)
SELECT '005001', '피드백', '005', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='005001');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '005002', '요금청구', '005', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='005002');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '005003', '계약/견적', '005', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='005003');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '005004', '안전/피해예방', '005', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='005004');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '005005', '뉴스레터', '005', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='005005');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '005006', '거래알림', '005', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='005006');

-- '배송' 하위
INSERT INTO category (id, name, parent_id, created_at)
SELECT '006001', '배송상태', '006', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='006001');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '006002', '배송예정', '006', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='006002');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '006003', '배송완료', '006', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='006003');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '006004', '배송실패', '006', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='006004');

-- '법적고지' 하위
INSERT INTO category (id, name, parent_id, created_at)
SELECT '007001', '수신동의', '007', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='007001');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '007002', '개인정보', '007', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='007002');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '007003', '약관변경', '007', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='007003');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '007004', '휴면 관련', '007', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='007004');

-- '업무알림' 하위
INSERT INTO category (id, name, parent_id, created_at)
SELECT '008001', '주문/예약', '008', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='008001');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '008002', '내부 업무 알림', '008', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='008002');

-- '쿠폰/포인트' 하위
INSERT INTO category (id, name, parent_id, created_at)
SELECT '009001', '쿠폰발급', '009', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='009001');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '009002', '쿠폰사용', '009', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='009002');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '009003', '포인트적립', '009', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='009003');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '009004', '포인트사용', '009', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='009004');

INSERT INTO category (id, name, parent_id, created_at)
SELECT '009005', '쿠폰/포인트안내', '009', NOW() WHERE NOT EXISTS (SELECT 1 FROM category WHERE id='009005');
