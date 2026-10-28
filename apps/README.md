# OTTer Shield | Applications

| 앱 | 용도 | 배포 |
|---|---|---|
| [사내 게시판](m1-intranet/README.md) | 직원 간 칭찬·점심 이야기 공유 | M1 한국 · M2 독일 |
| [OTT 메인](m3-ott/README.md) | 영상 탐색·재생 | M3 한국 API·DB · AWS 정적 웹 |
| [Store](m5-store/README.md) | 굿즈 조회·장바구니·모의 주문 | M5 캐나다 AWS |

## 사내 게시판 · OTTer Inside

![사내 게시판 메인 화면](m1-intranet/docs/evidence/ui-feed.png)

- 직원 번호 로그인 및 최초 비밀번호·닉네임 설정
- 칭찬·점심 게시글 등록, 본인 글 수정·삭제
- 칭찬 글 익명 작성, JPEG·PNG 사진 1장 첨부
- 피드형 목록과 페이지 이동, 현재 페이지 내 분류·검색

### 검증 자료

개발 PC 검증 · 2026-10-28

| 확인 항목 | 캡처 |
|---|---|
| 사진 첨부 화면 | [피드 내 사진 표시](m1-intranet/docs/evidence/ui-photo.png) |
| 백엔드 테스트 · 프론트엔드 빌드 | [테스트 성공](m1-intranet/docs/evidence/backend-test.png) · [빌드 성공](m1-intranet/docs/evidence/frontend-build.png) |
| 로그인 · 세션 유지 | [로그인 200](m1-intranet/docs/evidence/01-login-200.png) · [세션 조회 200](m1-intranet/docs/evidence/01-session-me-200.png) |
| 게시글 등록 · 수정 · 삭제 | [등록 201](m1-intranet/docs/evidence/02-post-create-201.png) · [수정 204](m1-intranet/docs/evidence/04-post-update-204.png) · [삭제 204](m1-intranet/docs/evidence/05-post-delete-204.png) |
| 입력 검사 · 접근 제한 | [공백 제목 400](m1-intranet/docs/evidence/06-blank-title-400.png) · [미로그인 401](m1-intranet/docs/evidence/08-unauthorized-401.png) |
| 사진 첨부 · 조회 · 삭제 후 접근 | [등록 201](m1-intranet/docs/evidence/09-post-image-create-201.png) · [조회 200](m1-intranet/docs/evidence/10-post-image-get-200.png) · [삭제 후 404](m1-intranet/docs/evidence/12-post-image-after-delete-404.png) |

[환경 설정](m1-intranet/README.md) · [전체 검증 캡처](m1-intranet/docs/evidence/README.md) · [테스트 기록·인계 확인](m1-intranet/docs/TEST_REPORT.md)

## OTT 메인

메인 화면: 미등록 · 아래 기능은 개발 예정 범위입니다.

- 회원가입·로그인
- 영상 목록·상세 정보 조회
- 로그인 회원의 영상 재생
- 한국어·영어 자막 제공 및 AI 자막 연동

[환경 설정](m3-ott/README.md) · [ERD](../design/erd/ott/README.md) · [클래스 다이어그램](../design/classdiagram/ott/README.md)

## Store

메인 화면: 미등록 · 아래 기능은 개발 예정 범위입니다.

- Store 자체 회원가입·로그인
- 굿즈 목록·상세 조회
- 장바구니 추가·수량 변경·삭제
- 전체 또는 선택 상품 모의 주문, 배송 정보 입력 및 주문 내역 조회

[환경 설정](m5-store/README.md) · [ERD](../design/erd/store/README.md) · [클래스 다이어그램](../design/classdiagram/store/README.md)
