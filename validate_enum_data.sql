-- =========================
-- varchar → enum 변환 전 데이터 검증 SQL
-- =========================

-- meal.restaurant 검증 (허용값: DODAM, DORMITORY, FOOD_COURT, SNACK_CORNER, HAKSIK, FACULTY)
SELECT DISTINCT restaurant, COUNT(*) as count
FROM meal
WHERE restaurant NOT IN ('DODAM','DORMITORY','FOOD_COURT','SNACK_CORNER','HAKSIK','FACULTY')
GROUP BY restaurant;

-- meal.time_part 검증 (허용값: MORNING, LUNCH, DINNER)
SELECT DISTINCT time_part, COUNT(*) as count
FROM meal
WHERE time_part NOT IN ('MORNING','LUNCH','DINNER')
GROUP BY time_part;

-- menu_category.restaurant 검증
SELECT DISTINCT restaurant, COUNT(*) as count
FROM menu_category
WHERE restaurant NOT IN ('DODAM','DORMITORY','FOOD_COURT','SNACK_CORNER','HAKSIK','FACULTY')
GROUP BY restaurant;

-- menu.restaurant 검증
SELECT DISTINCT restaurant, COUNT(*) as count
FROM menu
WHERE restaurant NOT IN ('DODAM','DORMITORY','FOOD_COURT','SNACK_CORNER','HAKSIK','FACULTY')
GROUP BY restaurant;

-- user.provider 검증 (허용값: EATSSU, KAKAO, APPLE)
SELECT DISTINCT provider, COUNT(*) as count
FROM user
WHERE provider NOT IN ('EATSSU','KAKAO','APPLE')
GROUP BY provider;

-- user.role 검증 (허용값: USER, ADMIN)
SELECT DISTINCT role, COUNT(*) as count
FROM user
WHERE role NOT IN ('USER','ADMIN')
GROUP BY role;

-- user.status 검증 (허용값: ACTIVE, INACTIVE)
SELECT DISTINCT status, COUNT(*) as count
FROM user
WHERE status NOT IN ('ACTIVE','INACTIVE')
GROUP BY status;

-- inquiry.status 검증 (허용값: WAITING, ANSWERED, HOLD)
SELECT DISTINCT status, COUNT(*) as count
FROM inquiry
WHERE status NOT IN ('WAITING','ANSWERED','HOLD')
GROUP BY status;

-- report.report_type 검증 (허용값: NO_ASSOCIATE_CONTENT, IMPROPER_CONTENT, IMPROPER_ADVERTISEMENT, COPY, COPYRIGHT, EXTRA)
SELECT DISTINCT report_type, COUNT(*) as count
FROM report
WHERE report_type NOT IN ('NO_ASSOCIATE_CONTENT','IMPROPER_CONTENT','IMPROPER_ADVERTISEMENT','COPY','COPYRIGHT','EXTRA')
GROUP BY report_type;

-- report.status 검증 (허용값: PENDING, IN_PROGRESS, RESOLVED, REJECTED, FALSE_REPORT)
SELECT DISTINCT status, COUNT(*) as count
FROM report
WHERE status NOT IN ('PENDING','IN_PROGRESS','RESOLVED','REJECTED','FALSE_REPORT')
GROUP BY status;

-- =========================
-- 전체 데이터 분포 확인
-- =========================

SELECT 'meal.restaurant' as table_column, restaurant as value, COUNT(*) as count FROM meal GROUP BY restaurant
UNION ALL
SELECT 'meal.time_part', time_part, COUNT(*) FROM meal GROUP BY time_part
UNION ALL
SELECT 'menu.restaurant', restaurant, COUNT(*) FROM menu GROUP BY restaurant
UNION ALL
SELECT 'user.provider', provider, COUNT(*) FROM user GROUP BY provider
UNION ALL
SELECT 'user.role', role, COUNT(*) FROM user GROUP BY role
UNION ALL
SELECT 'user.status', status, COUNT(*) FROM user GROUP BY status
ORDER BY table_column, value;