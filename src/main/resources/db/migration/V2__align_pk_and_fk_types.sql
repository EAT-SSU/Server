-- =========================
-- V2: PK/FK 타입 정렬 (int → bigint)
-- =========================
-- 실행 순서:
-- 1. FK 제약조건 해제
-- 2. PK 타입 변경
-- 3. FK 컬럼 타입 변경
-- 4. FK 제약조건 재생성
-- =========================

SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- 1. PK 타입 변경: int → bigint
-- =========================

ALTER TABLE college
    MODIFY college_id BIGINT NOT NULL AUTO_INCREMENT;

ALTER TABLE department
    MODIFY department_id BIGINT NOT NULL AUTO_INCREMENT;

-- =========================
-- 2. FK 컬럼 타입 변경: int → bigint
-- =========================

ALTER TABLE department
    MODIFY college_id BIGINT NOT NULL;

ALTER TABLE partnership
    MODIFY partnership_college_id BIGINT,
    MODIFY partnership_department_id BIGINT;

-- =========================
-- 3. FK 제약조건 복구
-- =========================

SET FOREIGN_KEY_CHECKS = 1;
