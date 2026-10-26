# M1·M2 | 사내 게시판

개발: M5 유은혜 · 배포: M1 유용수 / M2 윤서영

칭찬·점심 게시글을 공유하는 사내 서비스입니다. M1·M2는 같은 코드를 사용하고 DB·계정은 각각 분리합니다.

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

배포 준비: 웹 서버는 Nginx·React 빌드 파일, 앱 서버는 Java 21·실행 Jar, DB 서버는 MySQL을 준비합니다.

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
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<DB주소>:3306/intranet` |
| `SPRING_DATASOURCE_USERNAME` | DB 계정 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 |

환경변수는 백엔드 실행 시 설정합니다. DB 주소·계정은 배포 환경에 맞게 입력하고 비밀번호는 저장소에 올리지 않습니다.

## SQL

[① 테이블 생성](sql/01_schema.sql) → [② 테스트 데이터](sql/02_test_data.sql) → [③ 조회 확인](sql/03_verify.sql)

DB명: `intranet` · 가상 직원 **50명**(`TEST001`~`TEST050`) · 게시글 2개.

Workbench에서 위 순서대로 실행합니다. 테스트 데이터 파일을 다시 실행하면 없는 샘플만 추가됩니다. 복제 테스트는 원본 DB에서 변경한 뒤 복제 DB에 반영되는지 확인합니다.

샘플 비밀번호는 로그인용 해시가 아니며 미디어 경로도 가상 값입니다. DB 복제·백업·복원 확인용이며, SQL의 실제 MySQL 실행 검증은 아직 진행하지 않았습니다.

## 코드·설계

[백엔드](backend/) · [ERD](../../design/erd/intranet/README.md) · [클래스 다이어그램](../../design/classdiagram/intranet/README.md)
