#!/bin/bash
echo ">>> [1/3] 기존 8080 포트 실행 중인 자바 프로세스 종료 중..."
fuser -k 8080/tcp 2>/dev/null || true

echo ">>> [2/3] 메이븐 프로젝트 빌드 시작 (mvn clean package)..."
./mvnw clean package -DskipTests

echo ">>> [3/3] 스프링 부트 서버 백그라운드 무중단 실행 (nohup)..."
JAR_FILE=$(ls target/*.jar 2>/dev/null | head -n 1)
nohup java -jar "$JAR_FILE" > app.log 2>&1 &

echo ">>> [배포 완료] 서버가 정상적으로 기동되었습니다!"
echo ">>> 실시간 로그를 확인합니다: tail -f app.log"
tail -f app.log