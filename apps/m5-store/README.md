# M5 | Store 환경 설정

개발·배포: M5 유은혜 · 캐나다 AWS  
현재 실행 코드·SQL 등록 전. 아래 버전은 개발 기준.

## 버전

| 구성 | 적용 기준 |
|---|---|
| Java | Temurin JDK 21 |
| Spring Boot | 4.1.1 |
| MySQL | 8.0.46 기준 · RDS 엔진 버전은 배포 시 확정 |
| Node.js | 22.12 이상 |
| React / React DOM | 19.3.0 |
| EKS / Nginx | 배포 환경에 맞는 버전 |

## 회원 계정

| 항목 | 설정 |
|---|---|
| 등록 방식 | Store 자체 회원가입 화면 · 구현 예정 |
| OTT 연동 | 회원가입·로그인 별도 운영 |
| 비밀번호 | BCrypt 해시 저장 |
| 회원 DB | Store 전용 회원 정보 관리 · 최종 컬럼은 구현 시 확정 |

## DB

| 항목 | 설정 |
|---|---|
| DB 서버 | 비공개 Amazon RDS for MySQL |
| DB명·SQL | 코드 등록 시 확정 |
| 데이터 | 회원·상품·장바구니·주문 |
| 앱 DB 계정 권한 | Store DB의 SELECT·INSERT·UPDATE·DELETE |
| 접근 조건 | EKS의 Store API에서 RDS 접속 허용 |

## 환경변수

Spring Boot 구현에 반영할 설정 이름.

| 이름 | 값 |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<RDS주소>:3306/<Store DB명>` |
| `SPRING_DATASOURCE_USERNAME` | 앱 DB 계정 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 |
| `SERVER_PORT` | `8080` |

## 실행·배포 조건

| 대상 | 설정 |
|---|---|
| React / Spring Boot | 개별 컨테이너 · 캐나다 EKS 배포 |
| 웹 연결 | 공개 ALB · HTTPS · 웹과 `/api` 전달 경로 |
| DB 연결 | EKS → 비공개 RDS 3306 |
| 배포 | GitHub Actions · 이미지 저장소 · Argo CD 연동 |
| 상품 데이터 | 상품 정보·이미지 경로 사전 준비 |
| 백업 | RDS 백업·스냅샷 및 복원 환경 |

[앱 소개](../README.md) · [프론트](frontend/README.md) · [백엔드](backend/README.md)
