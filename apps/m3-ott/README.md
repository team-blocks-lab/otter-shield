# M3 | OTT 메인 웹·AI

개발: M5 유은혜 · 한국 앱·DB 배포: M3 홍은서 · AWS 정적 웹 배포: M5

영상 목록·상세 조회, 회원가입·로그인, 로그인 회원의 영상 재생을 제공하는 서비스입니다. Store와 회원가입·로그인은 별도로 운영합니다.

## 버전

| 항목 | 버전 |
|---|---|
| Java | Temurin JDK 21 |
| Spring Boot | 4.1.1 |
| MySQL | 8.0.46 |
| React / React DOM | 19.3.0 |
| Rocky Linux | 인프라 담당자 지정 |

React는 프론트 프로젝트에서 설치합니다. Spring Boot·JPA·MySQL 드라이버는 백엔드 프로젝트 의존성으로 관리합니다.

```bash
npm install --save-exact react@19.3.0 react-dom@19.3.0
```

배포 준비: React 빌드 파일은 AWS S3·CloudFront에, Java 21을 포함한 백엔드 컨테이너는 M3 Kubernetes에 배포합니다.

## 포트

| 용도 | 포트 |
|---|---|
| 웹 | 80 / 443 |
| Spring Boot | 8080 |
| MySQL | 3306 |

## 환경변수

| 이름 | 값 |
|---|---|
| `SERVER_PORT` | `8080` |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<DB주소>:3306/ott` |
| `SPRING_DATASOURCE_USERNAME` | DB 계정 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 |

환경변수는 백엔드 실행 시 설정합니다. DB 주소·계정은 배포 환경에 맞게 입력하고 비밀번호는 저장소에 올리지 않습니다.

## SQL

[① 테이블 생성](sql/01_schema.sql) → [② 테스트 데이터](sql/02_test_data.sql) → [③ 조회 확인](sql/03_verify.sql)

DB명: `ott` · 가상 회원 **20명**(`test_member01`~`test_member20`) · 영상 3개 · 자막 2개.

Workbench에서 위 순서대로 실행합니다. 테스트 데이터 파일을 다시 실행하면 없는 샘플만 추가됩니다. 복제 테스트는 원본 DB에서 변경한 뒤 복제 DB에 반영되는지 확인합니다.

샘플 비밀번호는 로그인용 해시가 아니며 미디어 경로도 가상 값입니다. DB 복제·백업·복원 확인용이며, SQL의 실제 MySQL 실행 검증은 아직 진행하지 않았습니다.

## 코드·설계

[프론트](frontend/README.md) · [백엔드](backend/README.md) · [AI](ai-service/README.md)

[ERD](../../design/erd/ott/README.md) · [클래스 다이어그램](../../design/classdiagram/ott/README.md)
