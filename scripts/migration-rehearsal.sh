#!/usr/bin/env bash
set -e

if [ "$#" -ne 5 ]; then
  echo "usage: $0 <db_url> <db_username> <db_password> <migration_dir> <rehearsal_container>" >&2
  exit 1
fi

DB_URL="$1"
DB_USERNAME="$2"
DB_PASSWORD="$3"
MIGRATION_DIR="$4"
REHEARSAL_CONTAINER="$5"

DB_URL_WITHOUT_PREFIX="${DB_URL#jdbc:mysql://}"
DB_HOST_PORT="${DB_URL_WITHOUT_PREFIX%%/*}"
DB_NAME_WITH_PARAMS="${DB_URL_WITHOUT_PREFIX#*/}"
DB_NAME="${DB_NAME_WITH_PARAMS%%\?*}"
DB_HOST="${DB_HOST_PORT%%:*}"
DB_PORT="${DB_HOST_PORT##*:}"
if [ "$DB_PORT" = "$DB_HOST_PORT" ]; then DB_PORT="3306"; fi

REHEARSAL_DB_PASSWORD="rehearsal"
DUMP_FILE="/tmp/${REHEARSAL_CONTAINER}.sql"
DUMP_TIMEOUT="20m"
RESTORE_TIMEOUT="20m"
MIGRATION_TIMEOUT="10m"

cleanup_rehearsal() {
  sudo docker rm -f "$REHEARSAL_CONTAINER" >/dev/null 2>&1 || true
  rm -f "$DUMP_FILE"
}
trap cleanup_rehearsal EXIT

echo "DB 덤프를 시작합니다: $DB_HOST:$DB_PORT/$DB_NAME"
timeout "$DUMP_TIMEOUT" sudo docker run --rm \
  -e MYSQL_PWD="$DB_PASSWORD" \
  mysql:8.0 mysqldump \
  --single-transaction \
  --quick \
  --routines \
  --triggers \
  --set-gtid-purged=OFF \
  -h "$DB_HOST" \
  -P "$DB_PORT" \
  -u "$DB_USERNAME" \
  "$DB_NAME" > "$DUMP_FILE"

echo "리허설 MySQL 컨테이너를 시작합니다: $REHEARSAL_CONTAINER"
sudo docker run -d \
  --name "$REHEARSAL_CONTAINER" \
  -e MYSQL_ROOT_PASSWORD="$REHEARSAL_DB_PASSWORD" \
  -e MYSQL_DATABASE="$DB_NAME" \
  mysql:8.0

for i in $(seq 1 30); do
  if sudo docker exec -e MYSQL_PWD="$REHEARSAL_DB_PASSWORD" "$REHEARSAL_CONTAINER" mysql -h 127.0.0.1 -P 3306 -uroot -e "SELECT 1" >/dev/null 2>&1; then
    break
  fi
  if [ "$i" -eq 30 ]; then
    echo "리허설 MySQL 컨테이너가 준비되지 않았습니다."
    exit 1
  fi
  sleep 2
done

echo "덤프를 리허설 DB에 복원합니다."
timeout "$RESTORE_TIMEOUT" sudo docker exec -i -e MYSQL_PWD="$REHEARSAL_DB_PASSWORD" "$REHEARSAL_CONTAINER" mysql -uroot "$DB_NAME" < "$DUMP_FILE"

echo "복제 DB에서 Flyway 마이그레이션 리허설을 실행합니다."
timeout "$MIGRATION_TIMEOUT" sudo docker run --rm \
  --network "container:$REHEARSAL_CONTAINER" \
  -v "$MIGRATION_DIR:/flyway/sql:ro" \
  -e FLYWAY_URL="jdbc:mysql://127.0.0.1:3306/$DB_NAME?allowPublicKeyRetrieval=true&useSSL=false" \
  -e FLYWAY_USER="root" \
  -e FLYWAY_PASSWORD="$REHEARSAL_DB_PASSWORD" \
  -e FLYWAY_LOCATIONS="filesystem:/flyway/sql" \
  -e FLYWAY_BASELINE_ON_MIGRATE="true" \
  -e FLYWAY_BASELINE_VERSION="1" \
  -e FLYWAY_OUT_OF_ORDER="true" \
  -e FLYWAY_CONNECT_RETRIES="10" \
  flyway/flyway:9.5.1 migrate
