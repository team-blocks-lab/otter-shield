# M1·M2 | 한국·독일 공통 사내 게시판 웹

개발: M5 유은혜 · 배포: M1 유용수 / M2 윤서영

## 사용 환경

| 항목 | 기준 |
|---|---|
| OS | Rocky Linux — 세부 버전은 서버 구축 시 기록 |
| Java | **21** — 개발 PC는 Temurin JDK 21 |
| Spring Boot | **4.1.1** — 생성한 프로젝트 기준 |
| DB 접근 | Spring Data JPA |
| DB | **MySQL 8.0.46** — DB 실습 기준. 게시판 학원 연결 확인, 집은 8.0.45. M3·M4는 동일 8.0.46으로 맞춘 뒤 서버에서 `SELECT VERSION()` 확인 |
| 화면 | React — 버전은 프론트 프로젝트 생성 후 기록 |
| 웹 서버 | Nginx |
| 백엔드 전달 형식 | 실행 가능한 Jar 기준, 최종 배포 방식은 인계 시 기록 |
| 로그인 | 게시판 전용 세션. OTT·Store 세션과 분리 |

한국·독일은 같은 코드를 사용하며 DB·계정·DNS·사진 저장 경로를 각각 분리합니다.

## 설치·준비 대상

| 서버 역할 | 필요한 프로그램·파일 |
|---|---|
| 웹 | Nginx, React 빌드 결과물 |
| 앱 | Java 21, 게시판 Jar |
| DB | MySQL, DB 생성 SQL·초기 직원 계정 데이터 |

배포만 하는 서버에는 IntelliJ가 필요하지 않습니다. React 빌드 결과물을 받는 경우 Node.js도 필요하지 않습니다. Spring Boot·JPA·MySQL Driver는 앱 의존성에 포함됩니다.

## 포트

| 구간 | 기준 포트 |
|---|---|
| 직원 브라우저 → 웹 | HTTP 80 / HTTPS 443 |
| 웹 → Spring Boot | TCP 8080 |
| Spring Boot → MySQL | TCP 3306 |

## 환경변수

| 환경변수 | 설정값 |
|---|---|
| `SERVER_PORT` | `8080` |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<DB주소>:3306/<DB명>` |
| `SPRING_DATASOURCE_USERNAME` | 서비스 전용 DB 계정 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 — 저장소에 등록하지 않음 |

DB 주소·DB명·계정은 M1·M2에서 각각 지정합니다. 사진 저장 경로·허용 프론트 주소 관련 설정은 코드 작성 시 추가합니다.

## 전달 예정

[backend](backend/)에 Spring Boot 초기 코드·Employee 엔티티 등록. Java 21·MySQL 8.0.45(집)/8.0.46(학원) 연결과 8080 서버 시작 확인. 로그인·게시글 API·사진 기능은 구현 중이며 아직 배포용 완성본이 아닙니다.

## 설계 자료

[사내 게시판 ERD](../../design/erd/intranet/README.md) · [사내 게시판 클래스 다이어그램](../../design/classdiagram/intranet/README.md)

[서비스별 코드 안내](../README.md) · [프로젝트 첫 화면](../../README.md)

## DB 생성·샘플 데이터 (2026-10-26)

| 순서 | 파일 | 용도 |
|---|---|---|
| 1 | [01_schema.sql](sql/01_schema.sql) | DB·테이블·제약조건 생성 |
| 2 | [02_test_data.sql](sql/02_test_data.sql) | 가상 직원 2명·게시글 2개 추가 |
| 3 | [03_verify.sql](sql/03_verify.sql) | 데이터 조회·복제 변경 확인 예시 |

MySQL Workbench에서 파일을 열고 순서대로 실행하거나 DB 서버에서 다음과 같이 실행합니다. 비밀번호는 프롬프트에 입력합니다.

```bash
mysql -u <DB관리계정> -p < 01_schema.sql
mysql -u <DB관리계정> -p < 02_test_data.sql
mysql -u <DB관리계정> -p < 03_verify.sql
```

- DB명: `intranet`. UTF-8(`utf8mb4`), InnoDB 사용
- 기존 테이블·데이터를 삭제하지 않음. `IF NOT EXISTS`는 기존 테이블 구조를 수정하지 않으므로 다른 구조가 있으면 먼저 비교
- 샘플 계정의 `!TEST_ONLY_NO_LOGIN`은 로그인 불가 표시이며 비밀번호 해시가 아님. DB 복제·백업·복원 테스트 전용. 로그인 테스트 계정은 BCrypt 구현 후 별도 제공
- 미디어 경로는 가상 값. 실제 사진·영상·VTT 파일은 포함하지 않음
- SQL은 현재 설계와 Employee 매핑 기준으로 작성. Post·OTT 엔티티 구현 시 매핑 대조 필요. MySQL 서버에서 이 SQL의 실행 검증은 아직 수행하지 않음

