# M3 | OTT 메인 웹·AI

개발: M5 유은혜 · 한국 앱·DB 배포: M3 홍은서 · AWS 정적 웹 배포: M5

## 사용 환경

| 항목 | 기준 |
|---|---|
| OS | Rocky Linux — 세부 버전은 서버 구축 시 기록 |
| Java | **21** — 컨테이너 이미지 내부 런타임 |
| Spring Boot | **4.1.1 기준 개발 예정** |
| DB 접근 | Spring Data JPA |
| DB | **MySQL 8.0.46** — DB 실습 기준. 게시판 학원 연결 확인, 집은 8.0.45. M3·M4는 동일 8.0.46으로 맞춘 뒤 서버에서 `SELECT VERSION()` 확인 |
| 화면 | React — AWS S3·CloudFront 배포, 버전은 프로젝트 생성 후 기록 |
| API 배포 | M3 Kubernetes |
| 로그인 | OTT 전용 세션. Store는 별도 회원가입·로그인 |
| CI/CD | GitHub Actions + Argo CD 또는 Jenkins — 배포 준비 일정에 선택 |

## 설치·준비 대상

| 대상 | 필요한 프로그램·파일 |
|---|---|
| Kubernetes 노드 | Kubernetes, 호환 컨테이너 런타임 |
| API | Java 21 포함 앱 컨테이너 이미지, 배포 매니페스트 |
| DB 서버 | MySQL, DB 생성 SQL·초기 데이터 |
| 프론트 | M5가 React 정적 빌드를 S3·CloudFront에 배포 |
| AI | FastAPI·STT/LLM 구성 예정 — Python 버전·이미지·포트 미정 |

노드에 앱 실행용 Java·IntelliJ를 별도로 설치할 필요는 없습니다. Java는 앱 이미지 안에 포함합니다. Kubernetes·런타임 버전은 인프라 담당자가 호환성을 맞춰 선정합니다.

## 포트

| 구간 | 기준 포트 |
|---|---|
| 사용자 → API 진입점 | HTTPS 443 |
| 내부 Service → Spring Boot 컨테이너 | TCP 8080 |
| Spring Boot → MySQL | TCP 3306 |

Service·Ingress·NodePort 설정은 Kubernetes 구성에 맞춰 지정합니다. DB·Redis는 백엔드 접근용입니다.

## 환경변수

| 환경변수 | 설정값 |
|---|---|
| `SERVER_PORT` | `8080` |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<DB주소>:3306/<DB명>` |
| `SPRING_DATASOURCE_USERNAME` | 서비스 전용 DB 계정 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 — 저장소에 등록하지 않음 |

비밀번호는 Kubernetes Secret으로 주입하고 저장소에 등록하지 않습니다. OTT·Store 공유 Redis·공통 쿠키 환경변수는 사용하지 않습니다. OTT 다중 Pod의 세션 유지 방식은 별도 확정 후 설정을 추가합니다.

## 코드 위치·전달 예정

| 구성 | 위치 |
|---|---|
| React | [frontend](frontend/README.md) |
| Spring Boot | [backend](backend/README.md) |
| FastAPI·AI | [ai-service](ai-service/README.md) |

현재 실행 코드·이미지 등록 전입니다. 위 포트·환경변수는 구현 기준이며 아직 실행 검증 전입니다. 이미지 레지스트리·태그·상태 확인 경로·배포 매니페스트는 코드와 함께 추가합니다.

## 설계 자료

[OTT 메인 ERD](../../design/erd/ott/README.md) · [OTT 메인 클래스 다이어그램](../../design/classdiagram/ott/README.md)

[서비스별 코드 안내](../README.md) · [프로젝트 첫 화면](../../README.md)

## DB 생성·샘플 데이터 (2026-10-26)

| 순서 | 파일 | 용도 |
|---|---|---|
| 1 | [01_schema.sql](sql/01_schema.sql) | DB·테이블·제약조건 생성 |
| 2 | [02_test_data.sql](sql/02_test_data.sql) | 가상 회원 3명·영상 3개·자막 2개 추가 |
| 3 | [03_verify.sql](sql/03_verify.sql) | 데이터 조회·복제 변경 확인 예시 |

MySQL Workbench에서 파일을 열고 순서대로 실행하거나 DB 서버에서 다음과 같이 실행합니다. 비밀번호는 프롬프트에 입력합니다.

```bash
mysql -u <DB관리계정> -p < 01_schema.sql
mysql -u <DB관리계정> -p < 02_test_data.sql
mysql -u <DB관리계정> -p < 03_verify.sql
```

- DB명: `ott`. UTF-8(`utf8mb4`), InnoDB 사용
- 기존 테이블·데이터를 삭제하지 않음. `IF NOT EXISTS`는 기존 테이블 구조를 수정하지 않으므로 다른 구조가 있으면 먼저 비교
- 샘플 계정의 `!TEST_ONLY_NO_LOGIN`은 로그인 불가 표시이며 비밀번호 해시가 아님. DB 복제·백업·복원 테스트 전용. 로그인 테스트 계정은 BCrypt 구현 후 별도 제공
- 미디어 경로는 가상 값. 실제 사진·영상·VTT 파일은 포함하지 않음
- SQL은 현재 설계와 Employee 매핑 기준으로 작성. Post·OTT 엔티티 구현 시 매핑 대조 필요. MySQL 서버에서 이 SQL의 실행 검증은 아직 수행하지 않음
- M3 원본에 스키마·데이터 준비 → M4 초기 동기화 및 MySQL 복제 설정 → M3 데이터 변경 → M4 반영 확인. 복제 초기화 도구의 절차에 따라 스키마·데이터를 함께 전달하며 Replica에 샘플 SQL을 중복 적용하지 않음
- SQL 파일만으로 복제·자동 장애 전환이 구성되지는 않음. binlog·고유 server_id·복제 계정·통신 허용은 M3·M4에서 설정. ProxySQL은 접속 라우팅용이며 복제 엔진이 아님
- 백업·복원은 행 수뿐 아니라 값과 외래키 관계까지 비교. 복원 파일에는 DB명 지정이 있으므로 운영 원본이 아닌 격리 인스턴스에서 시험

