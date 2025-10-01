#!/bin/bash
APP_NAME=jober-app
JAR_FILE_PATH=$(find /home/ubuntu/target -name "*.jar" | head -n 1)
LOG_FILE=/home/ubuntu/$APP_NAME.log

echo "Starting $APP_NAME with prod profile..."
nohup java -jar $JAR_FILE_PATH --spring.profiles.active=prod > $LOG_FILE 2>&1 &
