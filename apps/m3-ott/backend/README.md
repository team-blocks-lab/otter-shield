# M3 | OTT Spring Boot API

**상태**: 실행 코드 등록 및 M5 로컬 통합 테스트 완료 · **한국 M3 Linux/Kubernetes 배포는 미검증**

- 개발: M5 유은혜 / 한국 API·DB 배포: M3 홍은서
- 기술: Java 21, Spring Boot **4.1.2** (`build.gradle`), Spring Data JPA, MySQL (`ott`), BCrypt, 서버 세션
- 기본 개발 포트: **8081** (`application.properties`의 `SERVER_PORT` 기본값). 기존 문서의 8080은 배포 시 선택 가능한 포트이지 현재 코드 기본값이 아닙니다.

## 실행 준비

1. MySQL에 [`ott` 테이블 생성](../sql/01_schema.sql), 접속할 DB 계정과 접근 권한 준비. 임의로 운영 DB를 초기화하지 마세요.
2. Java 21 설치. Windows IntelliJ 프로젝트 루트는 `apps/m3-ott/backend`입니다.
3. IntelliJ Run Configuration 또는 실행 환경에서 아래 변수를 설정합니다. **비밀번호나 키를 Git에 올리지 마세요.**

| 환경변수 | 예시/의미 |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://127.0.0.1:3306/ott` (M3 서버에서는 실제 DB 주소 사용) |
| `SPRING_DATASOURCE_USERNAME` | DB 앱 계정 |
| `SPRING_DATASOURCE_PASSWORD` | DB 암호, 코드·문서에 실값 기록 금지 |
| `SERVER_PORT` | 선택; 기본 `8081`, 배포 시 `8080` 등 지정 가능 |
| `APP_MEDIA_BASE_URL` | 개발 예 `http://localhost:5175`; DB `video_key`·`subtitle_key` 앞에 붙는 미디어 기본 URL |

Windows CMD에서 기존 환경변수 설정 또는 IntelliJ 실행 설정 후:

```cmd
cd /d C:\otter-shield\apps\m3-ott\backend
gradlew.bat classes
gradlew.bat bootRun
```

Linux/Rocky(Gradle Wrapper 실행권한이 없으면 `chmod +x gradlew`):

```bash
cd apps/m3-ott/backend
./gradlew bootJar
java -jar build/libs/ott-0.0.1-SNAPSHOT.jar
```

위 Linux 실행은 **예시 인계 절차이며 M3 서버에서 아직 검증되지 않았습니다**. 실행 전에 Java·MySQL 연결·환경변수와 jar 생성 여부를 확인하세요.

## API 계약 (실제 소스 기준)

| HTTP | 경로 | 기능/주요 결과 |
|---|---|---|
| POST | `/api/auth/signup` | 이름·아이디·암호 회원가입 / 201, 중복 409, 검증 실패 400 |
| POST | `/api/auth/login` | 세션 생성 / 200, 인증 실패 401 |
| GET | `/api/auth/me` | 현재 회원 / 200 또는 401 |
| POST | `/api/auth/logout` | 세션 무효화 / 204 |
| GET | `/api/videos?page=0&size=12` | PUBLIC 목록, 생성일 최신순 / 200, 잘못된 페이지 400 |
| GET | `/api/videos/{videoId}` | PUBLIC 상세 / 200, PRIVATE·미존재 404 |
| GET | `/api/videos/{videoId}/playback` | 로그인 + PUBLIC 확인 후 미디어 URL·자막 / 200, 미로그인 401, PRIVATE·미존재 404 |

**재생 API 응답 구조**:

```json
{
  "videoUrl": "http://localhost:5175/media/elephants-dream.mp4",
  "subtitles": [
    {"lang": "en", "url": "http://localhost:5175/media/demo-en.vtt"},
    {"lang": "ko", "url": "http://localhost:5175/media/demo-ko.vtt"}
  ]
}
```

영상·자막 경로는 DB에 실제 저장된 파일 키와 `APP_MEDIA_BASE_URL`로 생성하므로 위 JSON은 로컬 **예시**입니다. 자막이 없다면 `subtitles: []`입니다. 미디어 기본 URL이 비어 있으면 503이 될 수 있습니다. API는 파일을 업로드하거나 파일 존재 여부를 확인하지 않습니다.

로그인 정보는 `JSESSIONID` 쿠키를 사용하는 서버 세션에 저장됩니다. 브라우저 새로고침 후 `GET /api/auth/me`로 복원합니다. `spring.jpa.hibernate.ddl-auto=none`이므로 테이블은 SQL로 먼저 준비합니다.

## 검증 완료 범위

- M5 Windows 로컬에서 React 연동 로그인/세션·로그아웃, 회원가입 후 DB 행과 BCrypt 접두어 확인.
- 재생 API 비로그인 401, 로그인 PUBLIC 200 + 영상/영·한 VTT 주소 반환, PRIVATE 상세와 재생 404.
- 기존 백엔드 테스트 캡처 12장과 신규 PRIVATE 차단 캡처 2장: [Evidence 목록](../docs/evidence/README.md).
- **이번 변경에서 `gradlew.bat classes` 컴파일 성공은 확인**. 전체 `gradlew.bat test`, M3 Linux/K8s 실행·CI 빌드는 별도 검증 필요.

## M3 인계 주의사항

- **Dockerfile, Kubernetes Deployment/Service, Secret, 배포 파이프라인은 아직 이 폴더에 준비되지 않았습니다.** Java 프로젝트 실행 가능과 K8s 배포 완료는 다릅니다.
- 프런트엔드는 [별도 안내](../frontend/README.md)에 따라 M5가 정적 웹(S3·CloudFront)에 배포; 이 백엔드는 M3 서버에 배포합니다.
- Vite 개발 프록시는 개발 PC에서만 적용됩니다. 정식 웹/API HTTPS 연결 시 리버스 프록시, SameSite/Secure 쿠키 정책과 CORS, 다중 Pod 세션 공유 설계가 필요합니다.
- **영상 재생 API의 인증 차단만으로 MP4 직접 URL이 보호되지는 않습니다.** 로컬 MP4는 공개 개발 파일이고, 운영 콘텐츠 접근 제어는 별도 구현해야 합니다.
- DB의 `02_test_data.sql` 가상 회원 암호는 로그인할 수 없습니다. 실제 가입 API로 생성한 회원만 BCrypt 로그인이 가능합니다.

[상위 OTT 인계](../README.md) · [DB 스키마](../sql/01_schema.sql) · [Evidence](../docs/evidence/README.md)
