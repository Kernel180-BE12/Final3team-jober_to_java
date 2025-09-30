#!/usr/bin/env bash
set -euo pipefail

APP_NAME="jober-app"
PID_FILE="/home/ubuntu/${APP_NAME}.pid"

PID=""
if [ -f "$PID_FILE" ]; then
  PID="$(cat "$PID_FILE" 2>/dev/null || true)"
fi
# 보조: PID 파일이 없을 때 pgrep 사용
if [ -z "${PID}" ]; then
  PID="$(pgrep -f "spring\.application\.name=${APP_NAME}" || true)"
fi

if [ -z "${PID}" ]; then
  echo "No running ${APP_NAME} process found"
  exit 0
fi

echo "Stopping ${APP_NAME}: ${PID}"
kill -TERM "${PID}" || true
for i in {1..20}; do
  kill -0 "${PID}" 2>/dev/null || break
  sleep 0.5
done
kill -KILL "${PID}" 2>/dev/null || true

rm -f "$PID_FILE" || true
echo "Stopped ${APP_NAME}"
