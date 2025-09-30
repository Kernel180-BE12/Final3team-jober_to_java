#!/usr/bin/env bash
set -euo pipefail

APP_NAME="jober-app"
JAR="$(ls -t /home/ubuntu/target/*.jar 2>/dev/null | head -1)"
LOG="/home/ubuntu/${APP_NAME}.log"
PID_FILE="/home/ubuntu/${APP_NAME}.pid"

if [ -z "${JAR:-}" ] || [ ! -f "$JAR" ]; then
  echo "JAR not found under /home/ubuntu/target/*.jar"
  exit 1
fi

# 1) 기존 프로세스 그레이스풀 종료
if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  OLD_PID="$(cat "$PID_FILE")"
  echo "Stopping existing ${APP_NAME}: ${OLD_PID}"
  kill -TERM "${OLD_PID}" || true
  # 최대 10초 대기
  for i in {1..20}; do
    kill -0 "${OLD_PID}" 2>/dev/null || break
    sleep 0.5
  done
  # 그래도 살아있으면 강제 종료
  kill -KILL "${OLD_PID}" 2>/dev/null || true
fi

# pgrep 보조(혹시 PID_FILE이 없거나 엉킨 경우)
PG_PID="$(pgrep -f "spring\.application\.name=${APP_NAME}" || true)"
if [ -n "${PG_PID}" ]; then
  echo "Killing stray ${APP_NAME} process: ${PG_PID}"
  kill -KILL ${PG_PID} 2>/dev/null || true
fi

# 2) 로그 초기화
: > "$LOG"

# 3) 완전 백그라운드로 기동 (세션 분리, 즉시 0 반환)
#    - JAVA_TOOL_OPTIONS, SPRING_APPLICATION_JSON 등은 워크플로우에서 전달됨
#    - SPRING_PROFILES_ACTIVE 기본 prod
nohup bash -lc "exec java \$JAVA_TOOL_OPTIONS -Dspring.application.name='${APP_NAME}' -jar '${JAR}' --spring.profiles.active=\${SPRING_PROFILES_ACTIVE:-prod}" >> "$LOG" 2>&1 &

NEW_PID=$!
echo "${NEW_PID}" > "$PID_FILE"
echo "Started ${APP_NAME} (pid ${NEW_PID}) with profile \${SPRING_PROFILES_ACTIVE:-prod}"

# 4) 즉시 성공 종료 (tail -f 같은 블로킹 동작 금지)
exit 0
