# OTT 검증 캡처

M5 개발 PC의 API·MySQL 및 React 연동 검증.  
환경: Java 21 · Spring Boot 4.1.2 · MySQL 8.0.46 · React 19.3.0

| 항목 | 확인 내용 | 캡처 |
|---|---|---|
| 회원가입 | 가입 201 · 중복 409 · 입력 오류 400 | [가입](01-signup-201.png) · [중복](02-signup-duplicate-409.png) · [입력](03-signup-validation-400.png) |
| 회원 DB 저장 | MySQL `member` 데이터 확인 | [DB](04-signup-db.png) |
| 로그인·세션 | 로그인 200 · 세션 조회 200 · 잘못된 로그인 401 | [로그인](05-login-200.png) · [세션](06-session-me-200.png) · [오류](07-login-invalid-401.png) |
| 로그아웃 | 로그아웃 204 · 이후 세션 조회 401 | [로그아웃](08-logout-204.png) · [재조회](09-session-after-logout-401.png) |
| 영상 목록 | 빈 목록 200 · 잘못된 페이지 400 | [목록](10-video-list-empty-200.png) · [오류](11-video-page-invalid-400.png) |
| 없는 영상 | 상세 조회 404 | [상세](12-video-not-found-404.png) |
| PRIVATE 영상 | 상세·재생 접근 각각 404 | [상세](13-private-detail-404.png) · [재생](14-private-playback-404.png) |

추가 로컬 검증: PUBLIC 영상 재생, 영어·한국어 샘플 VTT 및 썸네일 표시, 미로그인 재생 401, React 빌드·Spring Boot 컴파일 성공. 해당 화면·빌드 캡처는 별도 미등록.  
**미검증**: AI 자막 생성, Jenkins/Kubernetes 운영 배포, 실제 MP4 파일 접근 통제.

[앱 환경 설정](../../README.md) · [백엔드](../../backend/README.md)
