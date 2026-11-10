# OTTer Store

굿즈 조회 · Store 전용 회원가입 및 로그인 · 장바구니 · 모의 주문 및 주문 내역

## 실행 환경

| 구성 | 버전 및 포트 |
|---|---|
| 프런트 | Node.js 22 · React 18.3.1 · Nginx `8080` |
| 백엔드 | Java 21 · Spring Boot 4.1.2 · `8082` |
| DB | MySQL 8.0 · `store` |

## DB 및 환경변수

[스키마](sql/01_schema.sql) 적용 후 [테스트 상품](sql/02_test_data.sql) 등록. 회원 계정은 Store 화면에서 가입.

| 환경변수 | 설정 |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<RDS_ENDPOINT>:3306/store` |
| `SPRING_DATASOURCE_USERNAME` | Store DB 계정 |
| `SPRING_DATASOURCE_PASSWORD` | Store DB 비밀번호 |
| `SESSION_COOKIE_SECURE` | HTTPS 환경에서 `true` |

## 컨테이너 빌드

`apps/m5-store`에서 실행.

```bash
docker build -t otter-store-api:local ./backend
docker build -t otter-store-web:local ./frontend
```

## AWS 배포 구성

| 항목 | 설정 |
|---|---|
| 배포 | ECR 이미지 · EKS · ALB HTTPS |
| ALB 경로 | `/api`, `/api/*` → 백엔드 · 나머지 → 프런트 |
| 상태 확인 | 프런트 `/healthz` · 백엔드 `/actuator/health` |
| DB | 비공개 RDS · 백엔드에서 접속 |
| 상품 이미지 | S3 이미지 HTTPS 주소를 `goods.image_key`에 등록 |

[배포 진행 현황](https://github.com/team-blocks-lab/otter-shield/issues/12)
