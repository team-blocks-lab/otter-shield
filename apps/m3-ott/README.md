# M3 | OTT 환경 설정

개발: M5 유은혜 · 웹·API·DB 배포: M3 홍은서 · 영상 S3/CloudFront CDN: M5  
상태: **로컬 통합 검증 완료** · M3 Jenkins/Kubernetes 배포 및 AI 자막 생성 미완료

## 버전

| 구성 | 버전·준비 항목 |
|---|---|
| Java | Temurin JDK 21 |
| Spring Boot | 4.1.2 · Gradle |
| MySQL | 8.0.46 |
| Node.js | 22.12 이상 |
| React / React DOM | 19.3.0 |
| Vite | 8.3.5 |
| Python / FastAPI | AI 기능 개발 후 확정 |
| Kubernetes / Linux | M3 서버 환경 기준 |

## 회원 계정

| 항목 | 설정 |
|---|---|
| 회원가입 | 이름·로그인 ID·비밀번호 |
| 비밀번호 | BCrypt 해시 저장 |
| 인증 | 서버 세션 · 로그아웃 시 무효화 |
| 재생 권한 | 로그인 + PUBLIC 영상만 허용 |
| Store 연동 | 계정 별도 운영 |

## DB

| 항목 | 설정 |
|---|---|
| DB명 | `ott` |
| 테이블 | `member`, `video`, `subtitle` |
| SQL 순서 | [스키마](sql/01_schema.sql) → [테스트 데이터](sql/02_test_data.sql)(선택) → [검증](sql/03_verify.sql) |
| 테스트 데이터 | 샘플 회원 20명·영상 3개·자막 2개 · 회원 암호/미디어 키는 실사용 불가 |
| 접근 | M3 APP 서버에서 MySQL 3306 연결 |

## 환경변수

| 이름 | 값 |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<DB주소>:3306/ott` |
| `SPRING_DATASOURCE_USERNAME` | 앱 DB 계정 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 · GitHub 업로드 금지 |
| `SERVER_PORT` | `8081` · 개발 기본값 |
| `APP_MEDIA_BASE_URL` | 미디어 CDN 주소 · 로컬 데모는 `http://localhost:5175` |
| `VITE_USE_MOCK` | 실제 API 연동 시 `false` |
| `VITE_MEDIA_BASE_URL` | 썸네일 등 미디어 기본 주소 |

## 실행·배포 조건

| 대상 | 설정 |
|---|---|
| React | M3 웹 배포 · `npm ci` → `npm run build` · `dist/` |
| Spring Boot | Java 21 · Gradle 빌드 → M3 Kubernetes |
| 배포 자동화 | **Jenkins** 사용 결정 · 파이프라인/서버 배포 미검증 |
| 개발 연결 | React Vite 5174/5175 → `/api` 프록시 → Spring Boot 8081 |
| 서버 연결 | 웹 HTTPS → M3 API → DB 3306 |
| 영상 | M5가 S3 업로드·CloudFront CDN 제공 · 원본 MP4는 Git에 없음 |
| AI | FastAPI·STT/LLM 개발 예정 · 현재 한/영 자막은 샘플 VTT |

[앱 소개](../README.md) · [프론트](frontend/README.md) · [백엔드](backend/README.md) · [검증 캡처](docs/evidence/README.md) · [ERD](../../design/erd/ott/README.md)
