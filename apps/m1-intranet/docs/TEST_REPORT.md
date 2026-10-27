# M1·M2 사내 게시판 — API 테스트 기록

개발: M5 · 배포 및 인프라 검증: M1/M2  
대상: `apps/m1-intranet/backend` · Java 21 / Spring Boot 4.1.1 / MySQL 8.0.46

> 실제 실행 결과만 완료로 기록. 테스트 계정·DB·서버 환경에 따라 결과 구분.

## 확인된 개발 환경 및 테스트

| 검증 항목 | 확인 결과 | 증거 |
| --- | --- | --- |
| `PostServiceTest` 3개(타인 수정·삭제 차단, 익명 표시) | 통과(2026-10-27, 개발자 로컬 실행) | 캡처 추가 예정 |
| Spring Boot 실행 및 MySQL 연결 | 성공(2026-10-27, 개발자 로컬 로그) | 캡처 추가 예정 |
| `intranet` DB의 `employee`·`post` 생성, 샘플 직원 50명·글 2개 | 개발자 로컬 확인(2026-10-27) | 캡처 선택 |
| Postman 로그인·세션·CRUD 전체 연동 | 검증 진행 중 | 미첨부 |
| 공통 오류 응답(400/401/403/404) | 통합 재검증 전 | 미첨부 |
| M1·M2 실제 서버 배포 | 미검증 | 미첨부 |

## Postman 통합 테스트 체크리스트

기준 URL: `http://localhost:8080` (개발 PC 기준). 현재 검토용 브랜치의 게시글 경로는 **`/api/post`** (단수). 추후 코드 변경 시 문서와 함께 갱신.

| 항목 | 메서드·경로 | 기대 상태 | 실제 상태 | 증거 파일 |
| --- | --- | --- | --- | --- |
| 로그인 성공 및 세션 발급 | POST `/api/auth/login` | 200 | 미검증 | `evidence/api-login.png` |
| 게시글 목록 조회 | GET `/api/post` | 200 | 미검증 | `evidence/api-post-list.png` |
| 게시글 등록·DB 저장 | POST `/api/post` | 201 | 미검증 | `evidence/api-post-create.png` |
| 본인 게시글 수정 | PUT `/api/post/{postId}` | 204 | 미검증 | `evidence/api-post-update.png` |
| 본인 게시글 삭제 | DELETE `/api/post/{postId}` | 204 | 미검증 | `evidence/api-post-delete.png` |
| 잘못된 입력(점심 글 익명 등록) | POST `/api/post` | 400 | 미검증 | `evidence/api-validation-400.png` |
| 미로그인 요청 | GET `/api/post` | 401 | 미검증 | `evidence/api-unauthorized-401.png` |
| 타인 게시글 수정·삭제 차단 | PUT/DELETE `/api/post/{postId}` | 403 | 미검증 | `evidence/api-forbidden-403.png` |

- 성공 기준: HTTP 상태, 응답 JSON 및 필요한 경우 MySQL 데이터 변화를 함께 확인
- 세션 인증 검증: 로그인 요청과 후속 요청에서 동일한 세션 쿠키 사용
- 권한 검증: 별도 테스트 계정과 해당 계정이 작성하지 않은 게시글 사용
- 증거는 완료 항목 위주로 최소한만 첨부하고, 실제 결과·검증 일시·테스트 환경 기록
- 캡처의 비밀번호, 쿠키(`JSESSIONID`), DB 접속정보 및 기타 비밀값 마스킹
- M1·M2의 운영망 배포 증거는 각 담당자 인프라 기술서에서 관리하고 본 문서에는 인계·공통 API 검증 결과만 기재

## 증거 첨부 방법

1. 집에서 동일한 개발 브랜치로 이동 후 Postman 실행
2. 실제 요청·응답을 확인하고 민감정보를 가린 PNG 저장
3. `docs/evidence/`에 캡처 추가 및 위 표의 '실제 상태'·파일명 갱신
4. GitHub Issue #8 댓글에 이 문서 링크와 남은 오류·다음 작업 기록
