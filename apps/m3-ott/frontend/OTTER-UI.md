# OTTer 화면 구조와 백엔드 연동 (이 문서는 기존 README/SETUP.md를 대체하지 않는다)

## 실행
```cmd
npm install
copy .env.example .env
npm run dev          :: http://localhost:5174
```
- `.env`에서 `VITE_USE_MOCK=true`(기본) 이면 백엔드 없이 Mock 데이터로 동작한다. 데모 계정 `demo01` / `demo1234` (서버 인증 아님)
- 백엔드 연결: `VITE_USE_MOCK=false` + Spring Boot(기본 8081) 실행. `/api`는 vite 프록시로 전달되어 개발 중 CORS 설정이 필요 없다.
- 샘플 영상은 기존 그대로 `public/media/elephants-dream.mp4` (Git 제외). `npm run build` 후 `dist/media`의 mp4는 자동 삭제된다.

## 실제 백엔드 계약 (업로드된 Controller/DTO 기준으로 연결 완료)
| 화면 | API | 비고 |
|---|---|---|
| 목록 | `GET /api/videos?page&size` | `{videos,page,totalPages,totalElements}`, size 1~50 |
| 상세 | `GET /api/videos/{videoId}` | PRIVATE/없음 모두 404, 응답에 재생 키 없음 |
| 가입 | `POST /api/auth/signup` | 201 `{memberId}`, 아이디 `[a-z0-9]{4,30}`, 비밀번호 8~64 |
| 로그인 / 복원 / 로그아웃 | `POST /login`, `GET /me`, `POST /logout` | 세션 쿠키, 에러 `{status,message,fieldErrors}` |

화면에는 `thumbnailKey`를 `VITE_MEDIA_BASE_URL`(CloudFront) + 키로 변환해 쓴다. 값이 비어 있으면 색 면 포스터가 나온다.

## 아직 백엔드에 없는 것 (프런트가 가짜로 완성했다고 표시하지 않는다)
- **재생 API**: `GET /api/videos/{id}/playback` → `{videoUrl, subtitles:[{lang:"ko"|"en", url}]}` 를 제안한다. 로그인 확인과 PRIVATE 차단, 주소 발급은 서버에서 해야 한다.
- 그 전에는 재생 화면이 "재생 API가 아직 준비되지 않았습니다"를 보여 준다. 시연용으로 `VITE_LOCAL_DEMO_PLAYBACK=true` 이면 로그인 여부만 서버(`/me`)에 확인하고 로컬 mp4를 재생한다. 이는 서버의 재생 권한 검증이 아니다.
- 자막 목록 API, CORS 설정(웹/API 도메인이 다를 때), HTTPS 쿠키(`Secure; SameSite=None`)
- DB의 `[TEST]` 영상은 가상 키라 썸네일/재생 파일이 없다. Elephants Dream을 DB에 넣으려면 팀에서 video 행과 S3 키를 만들어야 한다.

## 바꿀 파일
`src/api/realApi.js`(응답 매핑·재생 API), `.env`(주소). UI 컴포넌트는 정규화된 모델만 본다.

## 기존 코드와의 관계
기존 `src/App.jsx`, `src/index.css`(단일 데모 화면)는 새 구조로 대체했다. 원본은 Git 이력에서 복원할 수 있다. `package.json`, `package-lock.json`, `.gitignore`, `SETUP.md`, `README.md`는 건드리지 않았다.
