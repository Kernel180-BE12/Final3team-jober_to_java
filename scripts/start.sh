#!/usr/bin/env bash
set -euo pipefail

# === 필수 환경변수 확인 ===
: "${JWT_SECRET:?JWT_SECRET missing}"
: "${REFRESH_PEPPER:?REFRESH_PEPPER missing}"
: "${DB_URL:?DB_URL missing}"
: "${DB_USER:?DB_USER missing}"
: "${DB_PASS:?DB_PASS missing}"

# === Spring Boot DB 정보 주입 ===
export SPRING_APPLICATION_JSON="$(cat <<JSON
{
  "spring": {
    "datasource": {
      "url": "${DB_URL}",
      "username": "${DB_USER}",
      "password": "${DB_PASS}"
    }
  }
}
JSON
)"

# === JWT 설정 ===
export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Djwt.secret=${JWT_SECRET} -Dsecurity.refresh.pepper=${REFRESH_PEPPER}"

APP_NAME="jober-app"
JAR="$(ls -t /home/ubuntu/target/*.jar 2>/dev/null | head -1)"
LOG="/home/ubuntu/${APP_NAME}.log"
PID_FILE="/home/ubuntu/${APP_NAME}.pid"

if [ -z "${JAR:-}" ] || [ ! -f "$JAR" ]; then
  echo "JAR not found under /home/ubuntu/target/*.jar"
  exit 1
fi

# 1) 기존 프로세스 종료
if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  OLD_PID="$(cat "$PID_FILE")"
  echo "Stopping existing ${APP_NAME}: ${OLD_PID}"
  kill -TERM "${OLD_PID}" || true
  for i in {1..20}; do
    kill -0 "${OLD_PID}" 2>/dev/null || break
    sleep 0.5
  done
  kill -KILL "${OLD_PID}" 2>/dev/null || true
fi

# pgrep 보조
PG_PID="$(pgrep -f "spring\.application\.name=${APP_NAME}" || true)"
if [ -n "${PG_PID}" ]; then
  echo "Killing stray ${APP_NAME} process: ${PG_PID}"
  kill -KILL ${PG_PID} 2>/dev/null || true
fi

# 2) 로그 초기화
: > "$LOG"

# 3) 백그라운드 실행
nohup bash -lc "exec java \$JAVA_TOOL_OPTIONS -Dspring.application.name='${APP_NAME}' -jar '${JAR}' --spring.profiles.active=\${SPRING_PROFILES_ACTIVE:-prod}" >> "$LOG" 2>&1 &

NEW_PID=$!
echo "${NEW_PID}" > "$PID_FILE"
echo "Started ${APP_NAME} (pid ${NEW_PID}) with profile \${SPRING_PROFILES_ACTIVE:-prod}"

exit 0
