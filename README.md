✅ README.md (팀 프로젝트 환경 세팅 가이드)
# Final3team Jober to Java (Backend)

## 🚀 프로젝트 개요
- Spring Boot + MySQL (Docker) 기반 백엔드 프로젝트
- `Kernel180-BE12` 조직 내 팀 레포지토리

---

## 📦 개발 환경
- **Java**: 21 (JDK 23 빌드, 타깃 21)
- **Spring Boot**: 3.5.5
- **Build Tool**: Maven
- **DB**: MySQL 8 (Docker 컨테이너)
- **IDE**: IntelliJ IDEA (권장)

---

## ⚙️ 실행 방법

### 1. 레포지토리 클론
```bash
git clone https://github.com/Kernel180-BE12/Final3team-jober_to_java.git
cd Final3team-jober_to_java

2. Docker로 MySQL 실행
docker compose up -d


기본 설정:

DB 이름: appdb

유저: root

비밀번호: rootpw

포트: 3307 (호스트) → 3306 (컨테이너)

3. Spring Boot 실행

IntelliJ에서 FinalProjectsApplication 실행 또는 터미널에서:

./mvnw spring-boot:run

4. 정상 동작 확인

DB Health 체크:
http://localhost:8080/health/db

👉 결과: {"ok":true}

CRUD 예제 (Person):

POST http://localhost:8080/people

{
  "name": "홍길동"
}


GET http://localhost:8080/people

[
  {
    "id": 1,
    "name": "홍길동"
  }
]

🗂️ 프로젝트 구조
src/main/java/com/example/final_projects
 ├── FinalProjectsApplication.java   # 메인 클래스
 ├── controller/                     # REST 컨트롤러
 ├── entity/                         # JPA 엔티티
 ├── repository/                     # JPA 레포지토리
 └── service/                        # 서비스 계층 (추가 예정)
src/main/resources
 ├── application.yml                 # DB/환경 설정
 └── ...

🐳 Docker 컨테이너 관리

상태 확인:

docker ps


로그 확인:

docker logs -f demo-mysql


컨테이너 중지:

docker compose down

🤝 Git 브랜치 전략

main: 배포/안정 버전

feature/*: 기능 개발용 브랜치

fix/*: 버그 수정 브랜치

예시:

git checkout -b feature/user-api
git push origin feature/user-api

👨‍👩‍👧 팀원 세팅 체크리스트

 JDK 21+ 설치 (23도 가능, 타깃은 21)

 IntelliJ 설치 및 프로젝트 Import

 Docker Desktop 설치 + 실행

 docker compose up -d 로 MySQL 실행

 application.yml 에 DB 포트(3307) 확인

 ./mvnw spring-boot:run 실행해서 /health/db 확인
