# M1·M2 | 사내 게시판

개발: M5 유은혜 · 배포: M1 유용수 / M2 윤서영

칭찬·점심 이야기를 공유하는 사내 서비스. M1·M2는 같은 코드를 사용하고 DB·직원 계정·사진 저장 경로는 각각 분리합니다.

## 실행 환경

Temurin JDK 21 · Spring Boot 4.1.1 · MySQL 8.0.46 · Node.js 22.12 이상 · React 19.3.0

Java·MySQL·Node.js를 설치하고, 프로젝트 의존성은 Gradle Wrapper와 `npm ci`로 설치합니다.

## DB 및 직원 계정

1. [01_schema.sql](sql/01_schema.sql) 실행 — `intranet` DB와 테이블 생성.
2. [02_test_data.sql](sql/02_test_data.sql) 실행(선택) — 복제·백업 실습용 직원 50명, 게시글 2개.
3. [03_verify.sql](sql/03_verify.sql) 실행 — 데이터 조회.

샘플 직원은 로그인 불가 값으로 등록됩니다. 로그인용 직원은 아래와 같이 별도로 생성합니다.

`backend`에서 초기 비밀번호·BCrypt 해시 생성:

```bash
# Rocky Linux
bash gradlew test --tests "com.otter.intranet.auth.TestPasswordGeneratorTest" --rerun-tasks --info
# Windows PowerShell
.\gradlew.bat test --tests "com.otter.intranet.auth.TestPasswordGeneratorTest" --rerun-tasks --info
```

출력의 `TEST_PASSWORD`는 초기 로그인에 사용하고, 같은 실행에서 나온 `PASSWORD_HASH`를 아래에 넣습니다. 비밀번호·실행 로그는 저장소에 올리지 않습니다.

```sql
USE intranet;
INSERT INTO employee (employee_number, password_hash, nickname, first_login)
VALUES ('EMP001', '<PASSWORD_HASH 값>', NULL, TRUE);
```

최초 로그인 시 새 비밀번호와 닉네임을 설정합니다. 일반 회원가입은 제공하지 않습니다.

## 백엔드 실행

DB 관리자가 앱 전용 MySQL 계정에 `intranet.*`의 SELECT·INSERT·UPDATE·DELETE 권한을 부여합니다. DB가 별도 서버라면 APP 서버에서 접속 가능한 계정과 네트워크를 설정합니다.

| 환경변수 | 설정값 |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<DB주소>:3306/intranet` |
| `SPRING_DATASOURCE_USERNAME` | 앱 전용 DB 계정 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 |
| `APP_UPLOAD_DIR` | 앱 실행 계정이 읽고 쓸 수 있는 사진 저장 절대 경로 |
| `SERVER_PORT` | `8080` (기본값) |

위 값을 실행 환경에 설정한 뒤 `backend`에서 실행합니다.

```bash
# Rocky Linux: JAR 생성 및 실행
bash gradlew bootJar
java -jar build/libs/intranet-0.0.1-SNAPSHOT.jar

# Windows PowerShell: 개발 실행
.\gradlew.bat bootRun
```

사진은 `APP_UPLOAD_DIR`에, 저장 이름은 DB의 `post.image_key`에 보관됩니다. 서버 재시작·재배포 후에도 폴더를 유지하고 DB와 함께 백업합니다.

## 프론트엔드 실행 및 배포

`frontend`에서 실행:

```bash
npm ci
npm run dev
```

개발 접속: `http://localhost:5173` — Vite가 `/api` 요청을 로컬 Spring Boot 8080으로 전달합니다.

서버 배포: `npm run build` → `dist/`를 Nginx로 제공하고, 같은 웹 주소의 `/api`를 APP 서버로 프록시합니다. 업로드 요청 한도는 앱의 6MB 요청을 수용하도록 설정합니다. 포트는 WEB 80/443 · APP 8080 · DB 3306입니다.

## 화면 및 기능

- 직원 로그인 → 최초 비밀번호·닉네임 설정 → 게시판 피드.
- 칭찬·점심 글 등록, 본인 글 수정·삭제. 익명 작성은 칭찬만 가능.
- 사진 1장 선택 첨부(JPEG·PNG, 최대 5MB). 글 수정 시 기존 사진 유지.
- 피드에서 사진·본문 확인, 이전·다음 페이지 이동.
- 전체·칭찬·점심·내 글 분류와 검색은 **현재 조회한 페이지 안에서만** 적용.

[피드 화면](docs/evidence/ui-feed.png) · [사진 화면](docs/evidence/ui-photo.png) · [테스트 및 M1·M2 인계 확인](docs/TEST_REPORT.md) · [ERD](../../design/erd/intranet/README.md) · [클래스 다이어그램](../../design/classdiagram/intranet/README.md)
