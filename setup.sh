#!/bin/bash
set -e

echo "=================================================="
echo ">>> [1/3] 기존 8080 포트 실행 중인 자바 프로세스 종료 중..."
fuser -k 8080/tcp 2>/dev/null || true

echo ">>> [2/3] 메이븐 프로젝트 빌드 시작 (mvn clean package)..."
chmod +x ./mvnw 2>/dev/null || true
./mvnw clean package -DskipTests

echo ">>> [3/3] 환경변수 로드 및 스프링 부트 서버 백그라운드 무중단 실행 (nohup)..."
# config/.env 파일이 존재하면 환경변수로 등록하여 Spring Boot에 전달
if [ -f "config/.env" ]; then
    export $(grep -v '^#' config/.env | xargs)
fi

JAR_FILE=$(ls target/*.jar 2>/dev/null | grep -v 'original' | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo ">>> [오류] target 폴더에 실행 가능한 .jar 파일이 없습니다!"
    exit 1
fi

nohup java -jar "$JAR_FILE" > app.log 2>&1 &

echo "=================================================="
echo ">>> [배포 완료] 서버가 정상적으로 기동되었습니다 ($JAR_FILE)!"
echo ">>> 실시간 로그를 확인합니다 (종료: Ctrl + C):"
echo "=================================================="
sleep 2
tail -f app.log