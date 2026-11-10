# M5 | OTTer Store

Store 전용 회원가입·로그인, 굿즈 목록, 장바구니, 모의 주문 및 주문 내역. OTT 계정과 별도로 운영한다.

| 구성 | 기준 |
|---|---|
| 백엔드 | Java 21 · Spring Boot 4.1.2 · 기본 포트 8082 |
| 프런트 | Node.js 22 · React 18.3.1 · 컨테이너 포트 8080 |
| DB | MySQL 8.0 · DB 이름 `store` |

## DB 및 환경변수

DB 관리자 계정으로 [스키마](sql/01_schema.sql)를 먼저 적용한다. 백엔드는 실행 시 스키마를 검사한다. 검증용 상품은 [샘플 SQL](sql/02_test_data.sql)로 추가할 수 있다. 회원은 Store 화면에서 직접 가입한다.

| 변수 | 배포 설정 |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<RDS_ENDPOINT>:3306/store` |
| `SPRING_DATASOURCE_USERNAME` | Store DB 앱 계정 |
| `SPRING_DATASOURCE_PASSWORD` | 실행 환경에서 주입 |
| `SERVER_PORT` | `8082` |
| `SESSION_COOKIE_SECURE` | HTTPS 환경에서 `true` |

## 컨테이너 빌드

`apps/m5-store`에서 실행한다.

```bash
docker build -t otter-store-api:local ./backend
docker build -t otter-store-web:local ./frontend
```

## AWS 배포 설정

두 이미지를 ECR에 올린 뒤 EKS에서 실행한다. ALB의 같은 HTTPS 도메인에서 `/api`와 `/api/*`는 백엔드로, 나머지 경로는 프런트로 전달한다. ALB 상태 검사는 웹 `/healthz`, API `/actuator/health`를 사용한다. RDS는 비공개로 두고 백엔드에서 접속한다.

첫 검증은 백엔드 Pod 1개로 시작한다. 현재 로그인 세션은 앱의 HTTP 세션을 사용한다. 상품 사진을 S3에 올린 뒤 브라우저에서 열리는 HTTPS 이미지 주소를 `goods.image_key`에 반영한다.

[앱 소개](../README.md) · [프런트](frontend/README.md) · [백엔드](backend/README.md) · [배포 이슈](https://github.com/team-blocks-lab/otter-shield/issues/12)
