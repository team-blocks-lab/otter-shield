# M3 | OTT 환경 설정

개발: M5 유은혜 · API·DB 배포: M3 홍은서 · AWS 정적 웹: M5  
현재 SQL 등록 상태. 아래 앱 버전·환경변수는 구현 기준이며 실행 코드 등록 후 대조 필요.

## 버전

| 구성 | 적용 기준 |
|---|---|
| Java | Temurin JDK 21 |
| Spring Boot | 4.1.1 |
| MySQL | 8.0.46 |
| Node.js | 22.12 이상 |
| React / React DOM | 19.3.0 |
| Python / FastAPI | AI 코드 등록 시 확정 |
| Rocky Linux / Kubernetes | M3 배포 환경 기준 |

## 회원 계정

| 항목 | 설정 |
|---|---|
| 등록 방식 | OTT 회원가입 화면 · 구현 예정 |
| 가입 정보 | 실명·로그인 ID·비밀번호 |
| 비밀번호 | BCrypt 해시 저장 |
| 인증 | 세션 기반 · 다중 Pod 운영 시 세션 공유 구성 필요 |
| Store 연동 | 회원가입·로그인 별도 운영 |

## DB

| 항목 | 설정 |
|---|---|
| DB명 | `ott` |
| 테이블 | `member`, `video`, `subtitle` |
| SQL 순서 | [01_schema.sql](sql/01_schema.sql) → [02_test_data.sql](sql/02_test_data.sql)(선택) → [03_verify.sql](sql/03_verify.sql) |
| 테스트 데이터 | 회원 20명·영상 3개·자막 2개 |
| 샘플 제한 | 회원 비밀번호는 로그인용 해시가 아님 · 미디어 키는 가상 값 |
| 앱 DB 계정 권한 | `ott.*`의 SELECT·INSERT·UPDATE·DELETE |
| 접근 조건 | M3 API에서 DB 접속 허용 |

## 환경변수

Spring Boot 구현에 반영할 설정 이름.

| 이름 | 값 |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<DB주소>:3306/ott` |
| `SPRING_DATASOURCE_USERNAME` | 앱 DB 계정 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 |
| `SERVER_PORT` | `8080` |

미디어 CDN 주소·AI API 주소·공급자 인증값의 환경변수 이름은 구현 시 확정.

## 실행·배포 조건

| 대상 | 설정 |
|---|---|
| React | AWS S3·CloudFront 정적 웹 배포 |
| Spring Boot | Java 21 컨테이너 · M3 Kubernetes 배포 |
| MySQL | 3306 · API 서버에서 접근 |
| 미디어 | 실제 영상·썸네일·자막 파일 및 CDN 연결 |
| 웹·API | HTTPS · API 전달 경로 · 세션 쿠키 전달 설정 |
| AI | FastAPI 실행 환경·공급자 계정 및 인증값 |

[앱 소개](../README.md) · [프론트](frontend/README.md) · [백엔드](backend/README.md) · [AI](ai-service/README.md) · [ERD](../../design/erd/ott/README.md)
