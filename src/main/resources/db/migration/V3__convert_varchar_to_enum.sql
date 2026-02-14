-- =========================
-- V3: varchar → enum 변환
-- =========================
-- ⚠️ 주의: 이 마이그레이션 실행 전 반드시 데이터 검증 필요
-- 실행: ./run_validation.sh
-- =========================

-- =========================
-- meal 테이블
-- =========================

ALTER TABLE meal
    MODIFY restaurant ENUM ('DODAM','DORMITORY','FOOD_COURT','SNACK_CORNER','HAKSIK','FACULTY'),
    MODIFY time_part ENUM ('MORNING','LUNCH','DINNER');

-- =========================
-- menu_category 테이블
-- =========================

ALTER TABLE menu_category
    MODIFY restaurant ENUM ('DODAM','DORMITORY','FOOD_COURT','SNACK_CORNER','HAKSIK','FACULTY');

-- =========================
-- menu 테이블
-- =========================

ALTER TABLE menu
    MODIFY restaurant ENUM ('DODAM','DORMITORY','FOOD_COURT','SNACK_CORNER','HAKSIK','FACULTY');

-- =========================
-- user 테이블
-- =========================

ALTER TABLE user
    MODIFY provider ENUM ('EATSSU','KAKAO','APPLE'),
    MODIFY role ENUM ('USER','ADMIN'),
    MODIFY status ENUM ('ACTIVE','INACTIVE');

-- =========================
-- inquiry 테이블
-- =========================

ALTER TABLE inquiry
    MODIFY status ENUM ('WAITING','ANSWERED','HOLD');

-- =========================
-- report 테이블
-- =========================

ALTER TABLE report
    MODIFY report_type ENUM ('NO_ASSOCIATE_CONTENT','IMPROPER_CONTENT','IMPROPER_ADVERTISEMENT','COPY','COPYRIGHT','EXTRA'),
    MODIFY status ENUM ('PENDING','IN_PROGRESS','RESOLVED','REJECTED','FALSE_REPORT');
