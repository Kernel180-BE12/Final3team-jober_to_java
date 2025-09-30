APP_NAME=jober-app
PID=$(pgrep -f $APP_NAME)

if [ -n "$PID" ]; then
  echo "Stopping $APP_NAME: $PID"
  kill -9 $PID
else
  echo "No running process found"
fi
