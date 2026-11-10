# Store | Frontend

React 18.3.1과 Vite를 사용한다. 개발 서버는 `/api`를 로컬 Spring Boot의 8082 포트로 전달한다.

```bash
npm ci
npm run dev
```

배포용 Dockerfile은 React를 빌드하고 Nginx 8080 포트에서 제공한다. 배포 환경의 `/api` 경로는 ALB에서 백엔드로 전달한다.

[앱 환경 설정](../README.md) · [앱 소개](../../README.md)
