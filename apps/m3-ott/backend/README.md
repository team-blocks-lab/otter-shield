# OTT | Backend

| 항목 | 내용 |
|---|---|
| 기술 | Java 21 · Spring Boot 4.1.2 · JPA · MySQL |
| 개발 포트 | `8081` · `SERVER_PORT`로 변경 가능 |
| DB 연결 | `ott` · `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` |
| 미디어 주소 | `APP_MEDIA_BASE_URL` · M5의 S3/CloudFront CDN 연결 |
| 실행·빌드 | `gradlew.bat bootRun` / `gradlew.bat bootJar` |
| 배포 | M3 홍은서 · Jenkins → Docker → Kubernetes 계획 |
| 상태 | 로컬 API 검증·Java 컴파일 성공 · M3 서버 배포 미검증 |

## API

| 경로 | 기능 |
|---|---|
| `POST /api/auth/signup` | 회원가입 |
| `POST /api/auth/login` · `POST /api/auth/logout` | 로그인·로그아웃 |
| `GET /api/auth/me` | 현재 로그인 회원 확인 |
| `GET /api/videos` · `GET /api/videos/{id}` | 공개 영상 목록·상세 |
| `GET /api/videos/{id}/playback` | 로그인·PUBLIC 확인 후 영상·한영 자막 URL 반환 |

비로그인 재생 401, PRIVATE 영상 상세·재생 404 확인. **API 접근 제한은 MP4 파일 자체의 접근 제한과 다릅니다.**

[앱 환경 설정](../README.md) · [SQL](../sql/01_schema.sql) · [검증 캡처](../docs/evidence/README.md)
