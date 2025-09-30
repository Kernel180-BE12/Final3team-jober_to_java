#!/bin/bash
set -euo pipefail

APP_NAME="jober-app"
LOG_FILE="/home/ubuntu/${APP_NAME}.log"

# 최신 JAR 1개만 선택
JAR_FILE="$(ls -1t /home/ubuntu/target/*.jar 2>/dev/null | head -n1 || true)"
if [[ -z "${JAR_FILE}" ]]; then
  echo "[start.sh] ❌ No JAR found under /home/ubuntu/target"
  exit 1
fi

# 필수 시크릿 가드 (값 출력 없음 / fail-fast)
: "${JWT_SECRET:?JWT_SECRET missing}"
: "${REFRESH_PEPPER:?REFRESH_PEPPER missing}"

# 기존 프로세스 종료(있으면)
if PID="$(pgrep -f "spring.application.name=${APP_NAME}" || true)"; then
  if [[ -n "${PID}" ]]; then
    echo "[start.sh] Stopping existing process: ${PID}"
    kill -9 ${PID} || true
  fi
fi

# 앱 기동 (prod)
echo "[start.sh] Starting ${APP_NAME} with JAR=${JAR_FILE}"
nohup java -Dspring.application.name="${APP_NAME}" \
  -jar "${JAR_FILE}" \
  --spring.profiles.active=prod \
  >> "${LOG_FILE}" 2>&1 &

echo "[start.sh] Started ${APP_NAME} (PID=$!) — log: ${LOG_FILE}"
sleep 2
tail -n 100 "${LOG_FILE}" || true
