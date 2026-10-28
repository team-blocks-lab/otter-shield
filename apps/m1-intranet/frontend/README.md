# 사내 게시판 프론트엔드

React 19.3.0 · Vite 8.3.5

```bash
npm ci
npm run dev
```

개발 접속: `http://localhost:5173`. `/api` 요청은 로컬 백엔드 8080으로 전달됩니다.

배포 빌드: `npm run build` → `dist/`. Nginx에서 정적 파일 제공 및 `/api` 프록시를 설정합니다.

[DB·계정·실행 안내](../README.md) · [검증 결과](../docs/TEST_REPORT.md)
