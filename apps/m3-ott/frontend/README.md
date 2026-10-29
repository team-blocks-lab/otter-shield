# M3 | OTT React 프런트엔드

**상태**: React 코드 등록·M5 로컬 통합 검증 완료 / **M3 OTT 웹 운영 배포는 미완료**.  
개발·인계: M5 유은혜 · **React 웹 배포 및 운영: M3 홍은서** · Spring Boot API/DB/Kubernetes: M3 홍은서.  
**OTT MP4 영상 S3 업로드·CloudFront CDN 연결 및 캐나다 Store 배포: M5 유은혜.**

## 실행 환경

- React 19.3.0 · Vite 8.3.5 · Node.js 22.12+ 권장
- 개발 프록시: `/api` → `http://localhost:8081` (실제 M3 Spring Boot 기본 포트)
- 실행 파일은 `src/`, 샘플 JPG/VTT는 `public/media/`. **MP4는 GitHub에 포함되지 않습니다.**

Windows CMD (기존 `.env`는 덮어쓰지 마세요):

```cmd
cd /d C:\otter-shield\apps\m3-ott\frontend
npm ci
copy .env.example .env
npm run dev -- --port 5175
```

`.env` 실제 API 연결 예:

```env
VITE_USE_MOCK=false
VITE_API_BASE_URL=
VITE_MEDIA_BASE_URL=http://localhost:5175
VITE_LOCAL_DEMO_PLAYBACK=false
```

- `VITE_USE_MOCK=true`는 화면 확인용 Mock 데이터. 인증·DB 테스트 증거가 아닙니다.
- `VITE_MEDIA_BASE_URL`은 DB `thumbnail_key`를 브라우저 접근 URL로 변환합니다.
- `VITE_LOCAL_DEMO_PLAYBACK=false`를 유지하여 실제 Spring Boot 재생 API로 접근 권한을 검사합니다.
- `public/media/elephants-dream.mp4` 원본은 별도 준비해야 합니다. 샘플 영어/한국어 VTT는 실제 STT/LLM 생성 결과가 아닙니다.

```cmd
npm run build
```

M5 PC에서 빌드 성공 확인, 생성물 `dist/`. Vite 설정에서 로컬 MP4는 빌드 결과에서 제외합니다.

## M3 React 웹 배포 — Jenkins 적용 예정 (미구축)

**M3 홍은서가 OTT React 웹 배포를 담당**합니다. 팀에서 선택한 자동화 도구는 Jenkins입니다. Jenkins Job/Jenkinsfile 및 React의 실제 호스팅 방식(정적 웹 서버/스토리지 등)은 M3 서비스망 구성에 따라 설정·검증해야 합니다. **M5 유은혜는 OTT React 배포 담당자가 아니며, OTT MP4 영상 S3 업로드·CloudFront CDN 연결과 캐나다 Store 배포를 담당**합니다. Jenkins와 운영 웹 배포는 아직 확인되지 않았습니다.

**주의**: 프로덕션에 배포한 정적 React 앱에는 Vite 개발 서버의 프록시 기능이 없습니다. `/api`의 HTTPS 백엔드 라우팅, 세션 쿠키/CORS, M5가 제공할 실제 MP4 S3/CloudFront 미디어 주소가 준비돼야 합니다. 운영 `VITE_MEDIA_BASE_URL`과 `VITE_API_BASE_URL`은 M3가 M5의 미디어 CDN 주소 및 M3 API 경로에 맞춰 빌드 시 설정합니다. 자격 증명과 비밀값을 Git에 올리지 마세요.

## 문서

[OTT 서비스/인계](../README.md) · [백엔드](../backend/README.md) · [Evidence](../docs/evidence/README.md) · [화면 구조/Mock 구분](OTTER-UI.md)

샘플 미디어: *Elephants Dream* © Blender Foundation (2006), CC BY 2.5. 재배포 시 저작자·라이선스 표시.
