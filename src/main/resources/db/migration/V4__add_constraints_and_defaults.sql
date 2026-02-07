-- =========================
-- V4: 제약조건 및 기본값 정렬
-- =========================
-- 1. NOT NULL 제약조건
-- 2. default 값
-- 3. rating 타입 통일
-- 4. unique 제약조건
-- =========================

-- =========================
-- 1. NOT NULL 제약조건
-- =========================

ALTER TABLE menu
    MODIFY is_discontinued BIT NOT NULL;

-- =========================
-- 2. default 값 설정
-- =========================

ALTER TABLE menu
    MODIFY like_count INT DEFAULT 0,
    MODIFY unlike_count INT DEFAULT 0;

-- =========================
-- 3. rating 타입 통일 (bigint → int)
-- =========================

ALTER TABLE review
    MODIFY rating INT;
