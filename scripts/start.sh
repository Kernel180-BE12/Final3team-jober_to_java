#!/bin/bash
APP_NAME=jober-app
JAR_FILE=/home/ubuntu/*.jar
LOG_FILE=/home/ubuntu/$APP_NAME.log

PID=$(pgrep -f $APP_NAME)
if [ -n "$PID" ]; then
  echo "Stopping existing process: $PID"
  kill -9 $PID
fi

nohup java -jar $JAR_FILE --spring.profiles.active=prod > $LOG_FILE 2>&1 &
echo "Started $APP_NAME with prod profile"
