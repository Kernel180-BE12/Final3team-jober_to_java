#!/bin/bash
APP_NAME=jober-app
JAR_FILE=target/*.jar
LOG_FILE=/home/ec2-user/$APP_NAME.log

PID=$(pgrep -f $APP_NAME)
if [ -n "$PID" ]; then
  echo "Stopping existing process: $PID"
  kill -9 $PID
fi

nohup java -jar $JAR_FILE > $LOG_FILE 2>&1 &
echo "Started $APP_NAME"
