# M1·M2 | 사내 게시판 환경 설정

개발: M5 유은혜 · 배포: M1 유용수 / M2 윤서영  
M1·M2 공통 코드 사용. DB·직원 계정·사진 저장 경로는 서버별 분리.

## 버전

| 구성 | 버전·준비 항목 |
|---|---|
| Java | Temurin JDK 21 |
| Spring Boot | 4.1.1 · Gradle 의존성 |
| MySQL | 8.0.46 |
| Node.js | 22.12 이상 |
| React / React DOM | 19.3.0 · npm 의존성 |
| Vite | 8.3.5 · npm 의존성 |
| Nginx / Rocky Linux | 배포 환경에 맞는 버전 |

## 직원 계정

| 항목 | 설정 |
|---|---|
| 등록 방식 | 관리자가 `employee`에 사전 등록 · 일반 회원가입 없음 |
| 필수 값 | `employee_number`, `password_hash`, `first_login` |
| 초기 값 | `nickname = NULL`, `first_login = TRUE` |
| 비밀번호 | BCrypt 해시 저장 · 평문 저장 금지 |
| 해시 생성 도구 | 백엔드 테스트 `TestPasswordGeneratorTest`의 비밀번호·해시 출력 |
| 최초 접속 | 초기 비밀번호 로그인 후 새 비밀번호·닉네임 설정 |

## DB

| 항목 | 설정 |
|---|---|
| DB명 | `intranet` |
| 테이블 | `employee`, `post` |
| SQL 순서 | [01_schema.sql](sql/01_schema.sql) → [02_test_data.sql](sql/02_test_data.sql)(선택) → [03_verify.sql](sql/03_verify.sql) |
| 테스트 데이터 | 직원 50명·게시글 2개 · 샘플 직원은 로그인 불가 |
| 앱 DB 계정 권한 | `intranet.*`의 SELECT·INSERT·UPDATE·DELETE |
| 접근 조건 | APP 서버에서 DB 접속 허용 |

## 환경변수

| 이름 | 값 |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<DB주소>:3306/intranet` |
| `SPRING_DATASOURCE_USERNAME` | 앱 DB 계정 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 |
| `APP_UPLOAD_DIR` | 읽기·쓰기 가능한 사진 저장 절대 경로 |
| `SERVER_PORT` | `8080` · 기본값 |

## 실행·배포 조건

| 대상 | 설정 |
|---|---|
| 백엔드 | Java 21 · Gradle Wrapper 빌드 · 실행 JAR |
| 프론트엔드 | `package-lock.json` 기준 의존성 설치 · 빌드 결과 `dist/` |
| 개발 연결 | React 5173 → Vite `/api` 프록시 → Spring Boot 8080 |
| 서버 연결 | Nginx 80/443 → `/api` 프록시 → APP 8080 → DB 3306 |
| 업로드 한도 | 사진 5MB · 요청 6MB · Nginx도 요청 크기 수용 |
| 파일 유지 | 업로드 폴더 재배포 시 유지 · DB와 함께 백업 |
| 인증 | 서버 세션 · 다중 APP 인스턴스 운영 시 세션·사진 공유 구성 필요 |

[앱 소개·UI](../README.md) · [테스트·인계 확인](docs/TEST_REPORT.md) · [ERD](../../design/erd/intranet/README.md)
