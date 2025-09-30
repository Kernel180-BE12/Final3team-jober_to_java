#!/bin/bash
set -euo pipefail

APP_NAME=jober-app
LOG_FILE="/home/ubuntu/${APP_NAME}.log"

# 최신 JAR 1개만 선택 (여러 개일 때 -jar 인자 충돌 방지)
JAR_FILE="$(ls -1t /home/ubuntu/target/*.jar 2>/dev/null | head -n1 || true)"
if [[ -z "${JAR_FILE}" ]]; then
  echo "[start.sh] ❌ No JAR found under /home/ubuntu/target"
  exit 1
fi

# 전달된 비밀값 길이만 로깅(값 자체는 출력 X)
echo "[start.sh] JWT_SECRET len=${#JWT_SECRET:-0}, REFRESH_PEPPER len=${#REFRESH_PEPPER:-0}"

# 필수 비밀값 가드: 없으면 즉시 실패 → 원인 명확
: "${JWT_SECRET:?JWT_SECRET missing}"
: "${REFRESH_PEPPER:?REFRESH_PEPPER missing}"

# 기존 프로세스 종료(있으면)
if PID="$(pgrep -f "spring.application.name=${APP_NAME}" || true)"; then
  if [[ -n "${PID}" ]]; then
    echo "[start.sh] Stopping existing process: ${PID}"
    kill -9 ${PID} || true
  fi
fi

# 백그라운드 기동 (prod)
echo "[start.sh] Starting ${APP_NAME} with JAR=${JAR_FILE}"
nohup java -Dspring.application.name="${APP_NAME}" \
  -jar "${JAR_FILE}" \
  --spring.profiles.active=prod \
  >> "${LOG_FILE}" 2>&1 &

echo "[start.sh] Started ${APP_NAME} (PID=$!) — log: ${LOG_FILE}"
sleep 2
tail -n 100 "${LOG_FILE}" || true
