# M3 | OTT 메인 웹·AI

개발: M5 유은혜 · 한국 앱·DB 배포: M3 홍은서 · AWS 정적 웹 배포: M5

## 버전

| 항목 | 버전 |
|---|---|
| Java | Temurin JDK 21 |
| Spring Boot | 4.1.1 |
| MySQL | 8.0.46 |
| Rocky Linux · React | 미정 |

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

## SQL

[① 테이블 생성](sql/01_schema.sql) → [② 테스트 데이터](sql/02_test_data.sql) → [③ 조회 확인](sql/03_verify.sql)

DB 테스트용 데이터이며 로그인·미디어 재생용은 아닙니다. SQL 실행 검증 전입니다.

## 코드·설계

[프론트](frontend/README.md) · [백엔드](backend/README.md) · [AI](ai-service/README.md)

[ERD](../../design/erd/ott/README.md) · [클래스 다이어그램](../../design/classdiagram/ott/README.md)
