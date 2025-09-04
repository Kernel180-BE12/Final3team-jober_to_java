# --- build stage ---
FROM eclipse-temurin:23-jdk AS build
WORKDIR /app

# 전체 복사 전에 .dockerignore로 빌드 컨텍스트를 최대한 줄여두세요
COPY . .

# Windows CRLF 및 실행권한 이슈 해결
# (temurin:23-jdk는 Debian/Ubuntu 계열이므로 apt 사용)
RUN apt-get update && apt-get install -y --no-install-recommends dos2unix \
 && rm -rf /var/lib/apt/lists/* \
 && dos2unix mvnw || true \
 && chmod +x mvnw

# 테스트 스킵 빌드
RUN ./mvnw -q -DskipTests package

# --- run stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# *-SNAPSHOT.jar에 한정하지 말고 *.jar 전체에서 하나만 복사(일반적으로 1개)
COPY --from=build /app/target/*.jar app.jar

# 선택: JVM 메모리/프로필
ENV JAVA_OPTS="-Xms256m -Xmx512m"
# 기본 프로필을 prod로, 외부에서 SPRING_PROFILES_ACTIVE 전달 시 덮어쓰기
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# exec로 PID 1에 java를 두어 정상 종료 시그널 처리
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]
