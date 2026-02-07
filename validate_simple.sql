-- =========================
-- 간단 검증: Enum에 없는 값 찾기
-- =========================
-- DB 클라이언트에서 이 쿼리를 복사해서 실행하세요
-- 결과가 0 rows면 정상!

-- meal.restaurant 검증
SELECT 'meal.restaurant' AS 테이블_컬럼, restaurant AS 불일치_값, COUNT(*) AS 개수
FROM meal
WHERE restaurant NOT IN ('DODAM','DORMITORY','FOOD_COURT','SNACK_CORNER','HAKSIK','FACULTY')
GROUP BY restaurant

UNION ALL

-- meal.time_part 검증
SELECT 'meal.time_part', time_part, COUNT(*)
FROM meal
WHERE time_part NOT IN ('MORNING','LUNCH','DINNER')
GROUP BY time_part

UNION ALL

-- user.provider 검증
SELECT 'user.provider', provider, COUNT(*)
FROM user
WHERE provider NOT IN ('EATSSU','KAKAO','APPLE')
GROUP BY provider

UNION ALL

-- user.role 검증
SELECT 'user.role', role, COUNT(*)
FROM user
WHERE role NOT IN ('USER','ADMIN')
GROUP BY role

UNION ALL

-- user.status 검증
SELECT 'user.status', status, COUNT(*)
FROM user
WHERE status NOT IN ('ACTIVE','INACTIVE')
GROUP BY status;

-- =========================
-- 결과 해석:
-- - 0 rows 또는 Empty set → ✅ 마이그레이션 진행 가능
-- - 데이터가 나오면 → ❌ 해당 데이터 정리 필요
-- =========================