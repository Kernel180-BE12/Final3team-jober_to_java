#!/bin/bash
APP_NAME=jober-app
PID=$(pgrep -f $APP_NAME)

if [ -z "$PID" ]; then
  echo "No running process found for $APP_NAME."
else
  echo "Stopping $APP_NAME (PID: $PID) with SIGTERM..."
  kill -15 $PID

  for i in {1..30}; do
    if ! kill -0 $PID 2>/dev/null; then
      echo "$APP_NAME stopped gracefully."
      exit 0
    fi
    sleep 1
  done

  echo "$APP_NAME did not stop gracefully. Forcing shutdown with SIGKILL..."
  kill -9 $PID
fi
