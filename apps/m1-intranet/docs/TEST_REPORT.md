# 사내 게시판 테스트 및 인계

기준일: 2026-10-28 · 개발 PC: Java 21 / Spring Boot 4.1.1 / MySQL 8.0.46  
아래 결과는 개발자 로컬 실행 기록과 등록된 캡처 기준입니다. M1·M2 서버 검증은 별도입니다.

## 개발 환경 검증

| 항목 | 결과 | 증빙 |
|---|---|---|
| 백엔드 테스트 | `gradlew.bat test` 성공 | [실행 결과](evidence/backend-test.png) |
| 프론트엔드 빌드 | `npm run build` 성공 | [빌드 결과](evidence/frontend-build.png) |
| React 화면 | 게시글 피드 및 첨부 사진 표시 확인 | [피드](evidence/ui-feed.png) · [사진](evidence/ui-photo.png) |

## API 검증

기준 주소: `http://localhost:8080`. 로그인과 후속 요청은 같은 세션 쿠키를 사용합니다.

| 항목 | 요청 | 확인 결과 | 증빙 |
|---|---|---|---|
| 로그인·세션 조회 | POST `/api/auth/login`, GET `/api/auth/me` | 200 | [로그인](evidence/01-login-200.png) · [세션](evidence/01-session-me-200.png) |
| 게시글 등록·조회 | POST / GET `/api/post` | 201 / 200, DB 저장 확인 | [등록](evidence/02-post-create-201.png) · [조회](evidence/03-post-feed-200.png) · [DB](evidence/03-post-db.png) |
| 본인 글 수정 | PUT `/api/post/{postId}` | 204, 목록·DB 반영 | [수정](evidence/04-post-update-204.png) · [조회](evidence/04-post-update-feed-200.png) · [DB](evidence/04-post-update-db.png) |
| 본인 글 삭제 | DELETE `/api/post/{postId}` | 204, DB 삭제 및 재삭제 404 | [삭제](evidence/05-post-delete-204.png) · [DB](evidence/05-post-delete-db.png) · [재삭제](evidence/05-post-delete-again-404.png) |
| 공백 제목·점심 익명 등록 | POST `/api/post` | 400 | [제목 검사](evidence/06-blank-title-400.png) · [익명 제한](evidence/06-anonymous-lunch-400.png) |
| 로그아웃·미로그인 접근 | POST `/api/auth/logout`, GET `/api/post` | 204 / 401 | [로그아웃](evidence/07-logout-204.png) · [접근 차단](evidence/08-unauthorized-401.png) |
| 사진 첨부 등록 | POST `/api/post` (multipart) | 201, DB 이미지 키 저장 | [등록](evidence/09-post-image-create-201.png) · [DB](evidence/image-db-before-delete.png) |
| 사진 조회 | GET `/api/post/{postId}/image` | 200, 사진 표시 | [사진 조회](evidence/10-post-image-get-200.png) |
| 사진 첨부 글 삭제 | DELETE `/api/post/{postId}` | 204, 이후 사진 API 404 | [삭제](evidence/11-post-image-delete-204.png) · [재조회](evidence/12-post-image-after-delete-404.png) |

최초 비밀번호·닉네임 설정 및 타인 글 수정·삭제 차단은 별도 API 캡처가 없습니다. 실제 업로드 파일 삭제는 404 응답만으로 확인할 수 없으므로 저장 폴더에서도 확인합니다.

## M1·M2 서버 인계 확인

각 담당자가 자신의 서버에서 아래 항목을 확인하고 결과와 기록 링크를 남깁니다.

- [ ] MySQL 테이블 및 앱 계정 준비, Spring Boot의 DB 연결 성공
- [ ] 직원 계정 생성, 로그인 및 최초 비밀번호·닉네임 설정
- [ ] React 화면에서 게시글 등록·조회·수정·삭제
- [ ] 다른 직원의 수정·삭제 거절(403), 미로그인 접근 거절(401)
- [ ] 사진 첨부·조회 및 글 삭제 후 실제 파일 삭제
- [ ] 재시작 후 사진 유지, DB와 사진 폴더 백업 범위 확인
- [ ] Nginx의 `/api` 프록시·사진 업로드 및 세션 유지 확인

| 구역 | 담당 | 서버 검증 결과 | 기록 링크 |
|---|---|---|---|
| M1 | 유용수 | 미확인 | — |
| M2 | 윤서영 | 미확인 | — |

[설정·실행 안내](../README.md) · [전체 증빙](evidence/README.md)
