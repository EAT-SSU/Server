# DB 마이그레이션 실행 가이드

## 📁 마이그레이션 파일 목록

```
src/main/resources/db/migration/
├── V2__align_pk_and_fk_types.sql          (PK/FK bigint 통일)
├── V3__convert_varchar_to_enum.sql        (varchar → enum 변환)
└── V4__add_constraints_and_defaults.sql   (제약조건 및 기본값)
```

## 🚀 실행 순서

### 1️⃣ 사전 준비 (필수)

#### ✅ 스냅샷 확인
- [ ] Dev DB 스냅샷 생성 완료
- [ ] Prod DB 스냅샷 생성 완료 (실제 배포 전)

#### ✅ 데이터 검증 (필수)
```bash
# varchar 데이터가 Enum과 일치하는지 검증
./run_validation.sh
```

**중요:** 검증 결과가 비어있어야 정상 (불일치 데이터 없음)

#### ✅ FK 제약조건명 확인 (선택)
```bash
# department, partnership의 FK 이름 확인
mysql -h <host> -u <user> -p <database> < check_fk_constraints.sql
```

---

### 2️⃣ Dev 환경 마이그레이션

#### 방법 1: Flyway 자동 실행 (권장)
```bash
# application-dev.yml의 spring.flyway 설정 확인
# spring.jpa.hibernate.ddl-auto: none 확인

# 애플리케이션 실행 시 자동으로 마이그레이션 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### 방법 2: 수동 실행
```bash
# V2 실행
mysql -h <host> -u <user> -p <database> < src/main/resources/db/migration/V2__align_pk_and_fk_types.sql

# 검증: PK/FK 타입 확인
mysql -h <host> -u <user> -p -e "
  SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = '<database>'
    AND COLUMN_NAME IN ('college_id', 'department_id')
  ORDER BY TABLE_NAME;
"

# V3 실행
mysql -h <host> -u <user> -p <database> < src/main/resources/db/migration/V3__convert_varchar_to_enum.sql

# 검증: Enum 타입 확인
mysql -h <host> -u <user> -p -e "
  SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = '<database>'
    AND DATA_TYPE = 'enum'
  ORDER BY TABLE_NAME;
"

# V4 실행
mysql -h <host> -u <user> -p <database> < src/main/resources/db/migration/V4__add_constraints_and_defaults.sql

# 검증: 제약조건 확인
mysql -h <host> -u <user> -p -e "
  SHOW CREATE TABLE college;
  SHOW CREATE TABLE menu;
  SHOW CREATE TABLE review;
"
```

---

### 3️⃣ 마이그레이션 검증

#### 애플리케이션 테스트
```bash
# 애플리케이션 재시작
./gradlew bootRun --args='--spring.profiles.active=dev'

# 로그 확인
# - Enum 매핑 오류 없는지
# - DB 연결 정상인지
# - API 호출 정상인지
```

#### DB 데이터 확인
```sql
-- Enum 값 조회 테스트
SELECT restaurant, time_part FROM meal LIMIT 10;
SELECT provider, role, status FROM user LIMIT 10;

-- PK/FK 타입 확인
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    COLUMN_TYPE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = '<database>'
  AND COLUMN_NAME IN ('college_id', 'department_id')
ORDER BY TABLE_NAME, COLUMN_NAME;

-- 제약조건 확인
SELECT
    TABLE_NAME,
    CONSTRAINT_NAME,
    CONSTRAINT_TYPE
FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = '<database>'
  AND TABLE_NAME IN ('college', 'menu', 'review');
```

---

### 4️⃣ Prod 환경 마이그레이션

#### 실행 전 체크리스트
- [ ] Dev 환경 마이그레이션 완료 및 검증
- [ ] Dev 환경 애플리케이션 정상 동작 확인
- [ ] Prod DB 스냅샷 생성 완료
- [ ] 데이터 검증 (validate_enum_data.sql) 완료
- [ ] 사용량이 적은 시간대 선택 (새벽 2-4시 권장)
- [ ] 롤백 계획 수립

#### 실행
```bash
# Prod 환경에서 동일하게 실행
./gradlew bootRun --args='--spring.profiles.active=prod'

# 또는 수동 실행
mysql -h <prod-host> -u <user> -p <database> < V2__align_pk_and_fk_types.sql
mysql -h <prod-host> -u <user> -p <database> < V3__convert_varchar_to_enum.sql
mysql -h <prod-host> -u <user> -p <database> < V4__add_constraints_and_defaults.sql
```

---

## ⚠️ 주의사항

### 1. metadata lock 방지
- 사용량이 적은 시간대에 실행
- 실행 중 다른 세션에서 DDL 작업 금지

### 2. 데이터 검증 필수
```bash
# V3 실행 전 반드시 검증
./run_validation.sh
```

### 3. FK 제약조건 처리
- V2에서 `SET FOREIGN_KEY_CHECKS = 0/1` 사용
- FK 제약조건은 자동으로 유지됨

### 4. 롤백 방법
```bash
# AWS RDS 스냅샷으로 복원
aws rds restore-db-instance-from-db-snapshot \
    --db-instance-identifier your-db-restored \
    --db-snapshot-identifier eatssu-dev-pre-migration-20260207
```

---

## 🐛 문제 발생 시

### 1. Enum 변환 실패
```
ERROR 1265: Data truncated for column 'restaurant' at row 1
```
**원인:** DB에 Enum에 없는 값이 존재
**해결:** `validate_enum_data.sql` 실행하여 불일치 데이터 찾기

### 2. PK 타입 변경 실패
```
ERROR 1833: Cannot change column: used in a foreign key constraint
```
**원인:** FK 제약조건이 걸려있음
**해결:** V2 파일에 `SET FOREIGN_KEY_CHECKS = 0` 이미 포함됨

### 3. unique 제약조건 실패
```
ERROR 1062: Duplicate entry for key 'UK_college_name'
```
**원인:** college.name에 중복 데이터 존재
**해결:** 중복 데이터 정리 후 다시 실행

---

## ✅ 완료 확인

- [ ] V2, V3, V4 모두 정상 실행
- [ ] flyway_schema_history 테이블에 기록 확인
- [ ] 애플리케이션 정상 동작 확인
- [ ] API 테스트 통과
- [ ] 로그에 오류 없음
