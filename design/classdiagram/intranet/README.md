# 사내 게시판 클래스 다이어그램

한국·독일 본사 직원이 사용하는 칭찬·점심 공유 게시판의 클래스 구조입니다.

## 주요 기능

- 세션 기반 직원 번호 로그인, 현재 로그인 계정 확인 및 로그아웃
- 최초 로그인 시 비밀번호·닉네임 설정과 설정 완료 전 게시판 접근 제한
- 칭찬·점심 글을 같은 화면에서 최신순·페이지 단위로 조회하는 피드
- 게시글 등록과 작성자 본인만 가능한 수정·삭제
- 칭찬 글의 익명 선택과 실제 작성자 정보 보호
- 선택 사진 1장 첨부와 수정 시 사진 유지·교체·삭제
- 제목·내용의 공백 입력 거절, 글 종류 검증 및 점심 글의 익명 등록 제한

## 주요 구조

| 구분 | 역할 |
| --- | --- |
| AuthController · AuthService · AuthInterceptor | 세션 로그인·로그아웃, 최초 설정 및 접근 검사 |
| PostController · PostService | 피드 조회, 게시글 등록·수정·삭제 및 작성자 권한 검사 |
| ImageStorageService | 사진 파일 저장·삭제와 조회 주소 생성 |
| EmployeeRepository · PostRepository | 직원·게시글의 DB 조회와 저장·삭제 |
| Employee · Post | 직원 계정과 게시글의 정보·상태 관리 |
| Request · Response DTO | 화면과 서버 사이의 요청·응답 데이터 전달 |

익명 글도 실제 작성자는 `Post.author`로 유지합니다. 응답의 `authorDisplayName`은 익명 여부에 따라 표시하고, `mine`은 현재 로그인한 직원의 글인지 나타냅니다. 수정·삭제 권한은 서버에서 별도로 확인합니다.

사진은 요청의 `image`로 받고, 게시글에는 저장 식별자인 `imageKey`를 보관합니다. 응답에는 화면에서 사용할 `imageUrl`을 전달합니다.

## Class Diagram

![사내 게시판 클래스 다이어그램](post_ClassDiagram.drawio.png)

[원본 이미지 보기](post_ClassDiagram.drawio.png)

[클래스 다이어그램 목록](../README.md) · [개발 코드](../../../apps/m1-intranet/README.md)
