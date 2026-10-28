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


## 최초 환경 설정 및 실행

M1·M2는 동일한 코드를 사용하지만 MySQL DB, 직원 계정, 서버 주소, 사진 저장 경로는 별도로 설정합니다.

### 1. MySQL DB 준비

MySQL 관리자 계정으로 접속한 뒤 아래 순서로 실행합니다.

1. `sql/01_schema.sql` — `intranet` DB 및 `employee`, `post` 테이블 생성
2. `sql/02_test_data.sql` — 복제·백업 실습용 직원 50명 및 게시글 2건 등록(선택)
3. `sql/03_verify.sql` — DB 데이터 조회

**애플리케이션 DB 계정 생성**

MySQL에서 다음 SQL 실행:

```sql
CREATE USER 'intranet_app'@'127.0.0.1'
IDENTIFIED BY '<개별 설정 비밀번호>';

GRANT SELECT, INSERT, UPDATE, DELETE
ON intranet.*
TO 'intranet_app'@'127.0.0.1';
```

위 명령은 DB와 Spring Boot가 같은 서버에 있고 `127.0.0.1`로 접속하는 예시입니다. DB 서버가 분리되어 있다면 `127.0.0.1` 대신 APP 서버의 접속 출발지 IP로 계정 접근 범위를 설정합니다.

MySQL 3306 포트는 필요한 APP 서버에서만 접근을 허용합니다.

### 2. 직원 로그인 계정 준비

일반 회원가입 기능은 제공하지 않으며, DB 관리자에게 등록된 직원만 로그인할 수 있습니다.

`employee` 테이블의 주요 항목:

| 컬럼 | 설정값 |
|---|---|
| `employee_number` | 직원 번호 (예: `EMP001`) |
| `password_hash` | 초기 비밀번호의 BCrypt 해시 |
| `nickname` | 최초 설정 전 `NULL` |
| `first_login` | 최초 등록 시 `TRUE` |

직원 등록 SQL 예시:

```sql
INSERT INTO employee (
    employee_number,
    password_hash,
    nickname,
    first_login
)
VALUES (
    'EMP001',
    '<BCrypt로 생성한 초기 비밀번호 해시>',
    NULL,
    TRUE
);
```

`<BCrypt로 생성한 초기 비밀번호 해시>`는 실제 BCrypt 해시로 바꿔야 합니다. 평문 비밀번호나 MySQL SHA2 값은 사용할 수 없습니다.

**최초 로그인 절차**

1. 관리자가 직원 번호 및 초기 비밀번호 준비
2. 직원이 초기 비밀번호로 로그인
3. 최초 로그인 화면에서 새 비밀번호·닉네임 설정
4. DB의 비밀번호 해시 변경 및 `first_login = FALSE` 반영
5. 이후 변경된 비밀번호로 로그인

`02_test_data.sql`의 50명은 로그인 가능한 계정이 아닌 DB 실습용 데이터입니다. 로그인 테스트용 계정은 별도로 준비해야 합니다.

### 3. Spring Boot 환경 설정

백엔드 실행 시 다음 환경변수를 설정합니다.

```cmd
cd /d C:\otter-shield\apps\m1-intranet\backend

set "SPRING_DATASOURCE_URL=jdbc:mysql://127.0.0.1:3306/intranet"
set "SPRING_DATASOURCE_USERNAME=intranet_app"
set "SPRING_DATASOURCE_PASSWORD=<설정한 DB 비밀번호>"
set "APP_UPLOAD_DIR=<사진 저장 디렉터리>"

gradlew.bat bootRun
```

- `SPRING_DATASOURCE_URL`: 실제 MySQL 서버 IP 및 DB 이름
- `SPRING_DATASOURCE_USERNAME`: 애플리케이션 DB 계정
- `SPRING_DATASOURCE_PASSWORD`: DB 계정 비밀번호
- `APP_UPLOAD_DIR`: 사진 저장 경로(최신 로컬 코드 기준 확인 필요)

실제 비밀번호 및 개인별 환경설정 파일은 GitHub에 등록하지 않습니다.

### 4. React 실행

별도 터미널에서 실행:

```cmd
cd /d C:\otter-shield\apps\m1-intranet\frontend

npm ci
npm run dev
```

개발 환경 접속 주소: `http://localhost:5173`

React의 `/api` 요청은 개발용 Vite 프록시를 통해 Spring Boot의 8080 포트로 전달됩니다.

### 5. 실제 서버 배포 시 확인

- APP 서버: Java 21, Spring Boot JAR 실행
- WEB 서버: React `npm run build` 결과물(`dist/`)을 Nginx로 제공
- Nginx: `/api` 요청을 Spring Boot 서버로 프록시
- DB 서버: MySQL 3306 접근 범위 제한
- 사진 저장: 지정한 디렉터리의 쓰기 권한 및 파일 유지 확인
- M1·M2: 서버별 DB 접속정보·직원 계정·업로드 경로 분리

### 6. 정상 동작 확인

1. 직원 계정 로그인 및 최초 설정
2. 게시글 목록 조회
3. 게시글 등록·수정·삭제
4. 사진 첨부·조회·삭제
5. 익명 표시 및 본인 게시글 권한 확인

검증 결과는 [TEST_REPORT.md](docs/TEST_REPORT.md)에 기록합니다.
