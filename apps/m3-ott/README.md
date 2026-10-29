# M3 | OTT 메인 서비스 — 실행·배포 인계

> 기준: 2026-10-29 · 개발 M5 유은혜 · 한국 API/DB 배포·운영 M3 홍은서 · React AWS S3/CloudFront 배포 M5 유은혜  
> 현재 코드 브랜치: [feat/m3-ott](https://github.com/team-blocks-lab/otter-shield/tree/feat/m3-ott) (영상 재생·자막 React 연동 커밋 `d3772bc`). **main 병합 및 M3 서버 배포는 별도 확인 필요.**

## 무엇이 구현됐나

- React OTT 홈·목록·상세·회원가입·로그인·영상 재생 화면 및 반응형 UI.
- Spring Boot 회원가입(BCrypt), 로그인/로그아웃, 세션 복원, 공개 영상 목록(최신순·페이지 단위)/상세 조회.
- 로그인 회원의 재생 정보 API `GET /api/videos/{videoId}/playback`: PUBLIC 영상만 재생 URL과 한·영 VTT 자막 주소 반환.
- MySQL `ott` 데이터베이스의 `member`, `video`, `subtitle` 사용. OTT 인증은 Store·사내 게시판과 별도.
- **M5 로컬 통합 검증 완료**: 회원가입→DB 저장, 로그인·새로고침 세션 유지·로그아웃, 비로그인 재생 401, PUBLIC 영상 재생·영/한 자막·썸네일 표시, PRIVATE 상세/재생 404. [검증 증거 및 범위](docs/evidence/README.md) 참조.
- **배포 자동화 결정**: M3 API와 M5 정적 웹 배포에 **Jenkins 사용 예정**. Jenkinsfile·Job·이미지 빌드/배포 작업은 미구축·미검증이며 기존 GitHub Actions/Argo CD 계획과 구분합니다.
- **아직 미구현/미검증**: FastAPI·STT·LLM 실제 자막 생성, AWS 웹 정식 배포, M3 Linux/Kubernetes 서버 배포, 운영 미디어 접근 제어 및 다중 Pod 세션 공유. 로컬 API 성공과 인프라 배포 완료를 혼동하지 않습니다.

## 코드와 담당

| 위치 | 역할 | 실행/배포 책임 |
|---|---|---|
| [frontend](frontend/README.md) | React 19.3.0 / Vite 8.3.5, 정적 웹 | M5: 개발·AWS S3/CloudFront 배포. M3: API 통합 협력 |
| [backend](backend/README.md) | Java 21 / Spring Boot **4.1.2** / JPA / MySQL | M5: 개발·인계. M3: 한국 서버·컨테이너/K8s 배포 |
| [sql](sql/01_schema.sql) | DB `ott` 스키마 및 데이터 | M3: DB 운영·복제/백업 담당 M4와 협력 |
| [ai-service](ai-service/README.md) | 향후 FastAPI·STT·LLM | 미구현 |

## 로컬에서 함께 실행하기 (Windows)

**전제**: MySQL `ott` 스키마 준비, DB 계정/접근 권한 준비, Java 21, Node.js/npm.

1. [백엔드 안내](backend/README.md)대로 DB 접속 환경변수(`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`)를 설정하고 Spring Boot 실행. 기본 개발 포트는 **8081** (`SERVER_PORT`로 변경 가능).
2. 로컬 영상 테스트 시 백엔드 환경변수 `APP_MEDIA_BASE_URL=http://localhost:5175` 설정. React에 MP4가 실제 존재해야 재생됨.
3. [프런트 안내](frontend/README.md)대로 `npm ci`, `.env` 설정 후 `npm run dev -- --port 5175`.
4. `http://localhost:5175`에서 회원가입·로그인·공개 영상 조회·재생. Vite 개발 서버의 `/api` 프록시가 기본 `localhost:8081`로 요청 전달.

**로컬 영상 파일은 GitHub에 없음**: `frontend/public/media/elephants-dream.mp4`는 `.gitignore` 대상입니다. GitHub만 내려받은 팀원은 원본 MP4를 별도 전달받거나 접근 가능한 승인된 S3/CloudFront 미디어 주소를 설정해야 합니다. DB에 저장된 `video_key`·`subtitle_key`와 실제 미디어 경로가 일치해야 합니다. 테스트 DB의 기본 `test/...` 미디어 키는 가상 경로입니다.

## M3 한국망 서버 배포 전 체크

1. M3 DB에 `ott` 테이블·접근 권한 구성. [스키마](sql/01_schema.sql) → [선택 테스트 데이터](sql/02_test_data.sql) → [조회 검증](sql/03_verify.sql). 테스트 회원의 `!TEST_ONLY_NO_LOGIN` 값은 실제 로그인 가능한 BCrypt 비밀번호가 아닙니다.
2. **Jenkins**에서 Git 소스 체크아웃 → Gradle 테스트/bootJar → Docker 이미지 빌드/레지스트리 Push → M3 Kubernetes 배포·롤아웃 확인을 수행하도록 파이프라인을 준비합니다. Spring Boot 컨테이너 이미지와 Dockerfile·K8s Deployment/Service·환경변수 Secret 설정도 준비·검증해야 합니다. **현재 이 폴더에는 인계용 실행 소스가 있지만 배포용 Dockerfile/K8s 매니페스트는 아직 등록되지 않았습니다.**
3. `SERVER_PORT`, `APP_MEDIA_BASE_URL`, MySQL 접속 정보, DNS/방화벽/HTTPS 경로 확정. 개발 기본 8081과 컨테이너 서비스 포트(설계상 8080 가능)를 구분.
4. 웹은 **M5가 Jenkins를 사용하여 S3/CloudFront에 정적 배포**, API는 **M3가 Jenkins를 사용하여 한국 Kubernetes에 배포**하도록 계획합니다. 두 파이프라인은 별개이며 아직 실제 실행 검증 전입니다. 정적 웹을 무조건 Kubernetes에 올리는 구조가 아닙니다.
5. 운영 웹에서 `/api`를 HTTPS API로 전달할 프록시/CloudFront 동작 설정과 세션 쿠키·CORS 정책 검증. 개발 Vite 프록시는 운영 배포에 포함되지 않습니다.
6. 다중 Pod 운영 시 세션 공유/고정 세션 등 별도 설계 필요. 현재 로컬 세션 검증만으로 다중 Pod HA를 주장하지 않습니다.
7. MP4 URL은 현재 로컬 데모처럼 공개 파일일 수 있습니다. **API의 401/404와 실제 미디어 파일 보호는 별개**입니다. 운영에서 필요하면 CloudFront 서명 URL/쿠키 및 S3 접근 제어 등을 추가 설계합니다.
8. M3 서버에서 API·DB·React 연동 시험 후 캡처·오류 해결 기록을 [Evidence](docs/evidence/README.md) 및 Notion 기술서에 추가합니다.

## 환경·설계

| 구분 | 확인된 기준 |
|---|---|
| Java / Spring Boot | Temurin JDK 21 / **4.1.2** (`backend/build.gradle` 기준) |
| MySQL | 8.0.46 (팀 실습 기준) |
| React | 19.3.0 |
| API | 개발 기본 `localhost:8081`; 서버 실행 시 `SERVER_PORT`로 변경 |
| 개발 웹 | Vite `localhost:5175` (실습에서 사용, 설정 기본은 5174) |
| DB | `ott` · `member`, `video`, `subtitle` |
| 미디어 | DB의 파일 키 + `APP_MEDIA_BASE_URL`(서버) / `VITE_MEDIA_BASE_URL`(웹 썸네일) |

[OTT ERD](../../design/erd/ott/README.md) · [클래스 다이어그램](../../design/classdiagram/ott/README.md) · [앱 전체 안내](../README.md)
