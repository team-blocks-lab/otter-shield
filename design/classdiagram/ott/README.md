# OTT 메인 클래스 다이어그램

회원가입·로그인과 영상 목록·상세·재생을 처리하는 OTT 서비스의 클래스 구조입니다.

## 주요 기능

- 회원가입 시 로그인 ID 중복 확인과 비밀번호 해시 저장
- 세션 기반 로그인, 현재 로그인 회원 확인 및 로그아웃
- 공개 영상 목록의 최신순·페이지 단위 조회와 상세 정보 조회
- 비로그인 사용자의 재생 요청 제한과 비공개 영상 접근 거절
- 영상 재생 주소와 제공되는 한국어·영어 자막 주소 전달

## 주요 구조

| 구분 | 역할 |
| --- | --- |
| AuthController · AuthService · AuthInterceptor | 회원가입·로그인, 현재 회원 확인 및 재생 요청의 인증 검사 |
| VideoController · VideoService | 공개 영상 목록·상세 조회와 로그인 회원의 재생 정보 조회 |
| MediaUrlService | 저장된 파일 키를 이용한 썸네일·영상·자막 조회 주소 생성 |
| MemberRepository · VideoRepository · SubtitleRepository | 회원·공개 영상·영상별 자막의 DB 조회와 회원 저장 |
| Member · Video · Subtitle | 회원 계정, 영상 정보 및 영상에 연결된 자막 정보 관리 |
| VideoStatus | PUBLIC·PRIVATE로 영상 공개 여부 구분 |
| Request · Response DTO | 화면과 서버 사이의 요청·응답 데이터 전달 |

목록·상세 응답에는 영상 재생 주소를 포함하지 않습니다. 로그인 확인을 거친 재생 요청에만 `PlaybackResponse`로 영상 주소와 자막 목록을 전달합니다.

`VideoListResponse`의 `page`·`size`·`hasNext`는 화면의 더보기 처리에 사용합니다. 자막이 없는 영상은 빈 자막 목록을 반환합니다.

## Class Diagram

![OTT 메인 클래스 다이어그램](OTT_ClassDiagram.drawio.png)

[원본 이미지 보기](OTT_ClassDiagram.drawio.png)

[클래스 다이어그램 목록](../README.md) · [개발 코드](../../../apps/m3-ott/README.md)
