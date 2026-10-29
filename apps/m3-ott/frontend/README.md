# OTT | Frontend

| 항목 | 내용 |
|---|---|
| 기술 | React 19.3.0 · Vite 8.3.5 |
| 개발 주소 | `http://localhost:5174` · 테스트 시 `5175` 사용 |
| API 연결 | `/api` → `http://localhost:8081` · Vite 개발 프록시 |
| 실제 API 모드 | `.env`에서 `VITE_USE_MOCK=false`, `VITE_LOCAL_DEMO_PLAYBACK=false` |
| 썸네일/CDN | `VITE_MEDIA_BASE_URL` · M5가 제공할 미디어 주소 |
| 실행·빌드 | `npm ci` → `npm run dev` / `npm run build` |
| 배포 | M3 홍은서 · Jenkins 기반 웹 배포 예정 |
| 영상 원본 | `public/media/*.mp4`는 Git 제외 · 별도 제공 필요 |

로컬 React·Spring Boot 연동 및 영상/한영 자막 표시 검증 완료. AWS 미디어 CDN·Jenkins 운영 배포는 별도 검증.

[앱 환경 설정](../README.md) · [검증 캡처](../docs/evidence/README.md) · [앱 소개·UI](../../README.md)
