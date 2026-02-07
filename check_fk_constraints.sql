-- =========================
-- FK 제약조건명 확인
-- =========================
-- 실행 방법: mysql -h <host> -u <user> -p <database> < check_fk_constraints.sql

-- department, partnership 테이블의 FK 제약조건 확인
SELECT
    CONSTRAINT_NAME AS 'FK 이름',
    TABLE_NAME AS '테이블',
    COLUMN_NAME AS '컬럼',
    REFERENCED_TABLE_NAME AS '참조 테이블',
    REFERENCED_COLUMN_NAME AS '참조 컬럼'
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = DATABASE()
  AND REFERENCED_TABLE_NAME IS NOT NULL
  AND TABLE_NAME IN ('department', 'partnership')
ORDER BY TABLE_NAME, CONSTRAINT_NAME;

-- =========================
-- 전체 FK 제약조건 확인 (참고용)
-- =========================
SELECT
    TABLE_NAME AS '테이블',
    CONSTRAINT_NAME AS 'FK 이름',
    COLUMN_NAME AS '컬럼',
    REFERENCED_TABLE_NAME AS '참조 테이블',
    REFERENCED_COLUMN_NAME AS '참조 컬럼'
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = DATABASE()
  AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, CONSTRAINT_NAME;
