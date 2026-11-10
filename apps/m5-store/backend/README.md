# Store | Backend

Java 21 · Spring Boot 4.1.2 · Spring Data JPA. DB에 스키마를 적용하고 환경변수를 주입한 뒤 실행한다.

```bash
./gradlew bootRun
```

Windows CMD에서는 `gradlew.bat bootRun`을 실행한다. 컨테이너 기본 포트는 8082이며 상태 확인 경로는 `/actuator/health`다.

[DB·환경변수·배포 설정](../README.md) · [앱 소개](../../README.md)
