# M3 OTT | API·React 통합 검증 Evidence

> 기준: 2026-10-29 · 검증 환경: M5 Windows 로컬 React(:5175) + Spring Boot(:8081) + MySQL `ott`. M3 한국 서버·Kubernetes 및 AWS 운영 배포의 검증 증거가 아닙니다.

## 실제 저장한 캡처 (14장)

| 번호 | 확인 항목 | 결과·캡처 |
|---|---|---|
| 01 | 회원가입 | [201 Created](01-signup-201.png) |
| 02 | 중복 ID 가입 | [409 Conflict](02-signup-duplicate-409.png) |
| 03 | 회원가입 입력 검증 | [400 Bad Request](03-signup-validation-400.png) |
| 04 | 회원가입 DB 저장 | [MySQL 조회](04-signup-db.png) |
| 05 | 로그인 | [200 OK](05-login-200.png) |
| 06 | 로그인 세션 확인 | [200 OK](06-session-me-200.png) |
| 07 | 잘못된 로그인 | [401 Unauthorized](07-login-invalid-401.png) |
| 08 | 로그아웃 | [204 No Content](08-logout-204.png) |
| 09 | 로그아웃 후 세션 | [401 Unauthorized](09-session-after-logout-401.png) |
| 10 | 영상 목록 비어 있을 때 | [200 OK](10-video-list-empty-200.png) |
| 11 | 잘못된 페이지 요청 | [400 Bad Request](11-video-page-invalid-400.png) |
| 12 | 없는 영상 상세 조회 | [404 Not Found](12-video-not-found-404.png) |
| 13 | PRIVATE 영상 상세 접근 | [404 Not Found](13-private-detail-404.png) |
| 14 | PRIVATE 영상 재생 접근 | [404 Not Found](14-private-playback-404.png) |

## 2026-10-29 추가 로컬 통합 검증 (대화·실행 결과 기반)

- React 실제 API 모드(`VITE_USE_MOCK=false`)에서 회원가입 → Spring Boot → MySQL `member` 저장 확인.
- 로그인·새로고침 후 세션 유지·로그아웃·로그아웃 후 401 확인.
- `GET /api/videos/1/playback`: 비로그인 **401**, 로그인한 회원은 **영상 URL과 en/ko 자막 URL이 포함된 응답** 확인.
- MySQL `video`: Elephants Dream PUBLIC 영상 등록, PRIVATE 테스트 영상 별도 등록.
- MySQL `subtitle`: 데모 `en`, `ko` VTT 키 등록.
- React 영상 재생, 영·한 자막 전환, 썸네일 표시를 사용자 로컬 환경에서 확인.
- `npm run build` 성공, `gradlew.bat classes` 성공. **이 단계에서 전체 자동 테스트 `gradlew.bat test` 성공은 별도로 확인되지 않았습니다.**

위 통합 과정 중 **이 폴더에 신규로 저장된 캡처는 13·14번 두 건**입니다. 재생 200 JSON, React 영상/자막 화면, 빌드 결과는 별도 캡처 미등록이므로 이 문서에서 캡처가 있다고 표시하지 않습니다. 추후 필요하면 실제 화면 증거를 추가합니다.

## 보안 검증 범위와 한계

- 비로그인 `/playback` 401 및 PRIVATE 상세/재생 404는 **Spring Boot API 권한 검사**의 결과입니다.
- 로컬 샘플 `public/media/*.mp4`는 직접 URL로 접근할 수 있어 **MP4 원본 파일의 보호까지 입증하지 않습니다**. AWS에서 보호가 필요하면 S3 접근 통제·CloudFront 서명 URL/쿠키 등 별도 검증 필요.
- 샘플 영어·한국어 VTT는 **UI 자막 선택 검증용**이며 STT/LLM 생성 결과가 아닙니다.
- M3 Kubernetes, Jenkins, 정적 웹 CloudFront 운영 배포, 다중 Pod 세션 공유, DR은 별도 구축·증거 필요.

[OTT 실행·인계](../../README.md) · [백엔드 API 안내](../../backend/README.md) · [프런트 안내](../../frontend/README.md)
