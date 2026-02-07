#!/bin/bash

# =========================
# Enum 데이터 검증 스크립트
# =========================

echo "=========================================="
echo "Enum 데이터 검증 시작"
echo "=========================================="
echo ""

# 사용자에게 환경 선택
read -p "검증할 환경을 선택하세요 (dev/prod): " ENV

if [ "$ENV" != "dev" ] && [ "$ENV" != "prod" ]; then
    echo "오류: 'dev' 또는 'prod'만 입력 가능합니다."
    exit 1
fi

# DB 접속 정보 입력
read -p "DB 호스트를 입력하세요: " DB_HOST
read -p "DB 사용자명을 입력하세요: " DB_USER
read -sp "DB 비밀번호를 입력하세요: " DB_PASS
echo ""
read -p "DB 이름을 입력하세요: " DB_NAME

echo ""
echo "[$ENV 환경] 데이터 검증을 시작합니다..."
echo ""

# 검증 쿼리 실행
mysql -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" < validate_enum_data.sql > validation_result_${ENV}_$(date +%Y%m%d_%H%M%S).txt 2>&1

if [ $? -eq 0 ]; then
    echo "✅ 검증 완료! 결과 파일을 확인하세요: validation_result_${ENV}_$(date +%Y%m%d_%H%M%S).txt"
    echo ""
    echo "⚠️  중요: 결과 파일에서 아래 내용을 확인하세요"
    echo "   - 불일치 데이터가 있는 경우: 데이터 정리 후 다시 검증"
    echo "   - 결과가 비어있는 경우: varchar → enum 변환 진행 가능"
    echo ""
else
    echo "❌ 검증 실패. 접속 정보를 확인하세요."
    exit 1
fi
