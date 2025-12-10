-- member
INSERT INTO member (name, nickname, email, member_code, member_status, member_role, password, image_url, created_at, updated_at, inactivated_at)
VALUES ('홍길동', '홍야치', 'test1@test.com', 'testcode1', 'ACTIVE', 'ROLE_USER', 'testpassword', 'default.png', '2025-12-08 15:42:41', '2025-12-08 15:42:41', null);

INSERT INTO member (name, nickname, email, member_code, member_status, member_role, password, image_url, created_at, updated_at, inactivated_at)
VALUES ('전우치', '전야치', 'test2@test.com', 'testcode2', 'ACTIVE', 'ROLE_USER', 'testpassword', 'default.png', '2025-12-08 15:42:41', '2025-12-08 15:42:41', null);

INSERT INTO member (name, nickname, email, member_code, member_status, member_role, password, image_url, created_at, updated_at, inactivated_at)
VALUES ('박우진', '박야치', 'test3@test.com', 'testcode3', 'ACTIVE', 'ROLE_USER', 'testpassword', 'default.png', '2025-12-08 15:42:41', '2025-12-08 15:42:41', null);

-- favorite
-- 회원1
INSERT INTO favorite (member_id, product_code, period_type, created_at, updated_at)
VALUES (1, 'KM-411-01-04', 'DAILY', '2025-12-08 15:46:33', '2025-12-08 15:46:33');

INSERT INTO favorite (member_id, product_code, period_type, created_at, updated_at)
VALUES (1, 'KM-411-01-04', 'WEEKLY', '2025-12-08 15:51:31', '2025-12-08 15:51:31');

INSERT INTO favorite (member_id, product_code, period_type, created_at, updated_at)
VALUES (1, 'KM-411-01-04', 'MONTHLY', '2025-12-08 15:51:31', '2025-12-08 15:51:31');

INSERT INTO favorite (member_id, product_code, period_type, created_at, updated_at)
VALUES (1, 'KM-411-01-04', 'YEARLY', '2025-12-08 15:51:31', '2025-12-08 15:51:31');

INSERT INTO favorite (member_id, product_code, period_type, created_at, updated_at)
VALUES (1, 'KM-411-01-05', 'DAILY', '2025-12-08 15:51:31', '2025-12-08 15:51:31');

-- 회원2
INSERT INTO favorite (member_id, product_code, period_type, created_at, updated_at)
VALUES (2, 'KM-411-01-04', 'DAILY', '2025-12-08 15:51:31', '2025-12-08 15:51:31');

INSERT INTO favorite (member_id, product_code, period_type, created_at, updated_at)
VALUES (2, 'KM-411-01-05', 'DAILY', '2025-12-08 15:51:31', '2025-12-08 15:51:31');