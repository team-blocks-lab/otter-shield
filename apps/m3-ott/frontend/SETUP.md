# OTTer M3 React — 첫 실행 안내

이 파일은 React 영상 재생 데모의 실행 안내입니다. 기존 `frontend/README.md`는 보존합니다.

## 1. 준비

- Node.js / npm (M1 React 프로젝트 실행 환경과 동일하게 사용)
- 다운로드한 `ElephantsDream.zip`의 `ed_1024.mp4`
- 작업 폴더: `C:\otter-shield\apps\m3-ott\frontend`

## 2. 영상 복사

ZIP 압축을 푼 뒤 `ed_1024.mp4`를 아래 위치로 복사하고 이름을 변경합니다.

`C:\otter-shield\apps\m3-ott\frontend\public\media\elephants-dream.mp4`

해당 경로의 MP4는 `.gitignore`에서 제외됩니다. 샘플 영상 원본을 GitHub에 커밋하지 마세요.

## 3. 실행 (Windows CMD)

```cmd
cd /d C:\otter-shield\apps\m3-ott\frontend
npm install
npm run dev
```

주소: http://localhost:5174 (M1 게시판 5173과 충돌하지 않도록 분리)

## 4. 화면 확인

1. OTTer 홈의 `지금 재생` 클릭
2. 브라우저 플레이어에서 영상·영어 음성 확인
3. 영상 창 닫기, 콘텐츠 카드 재생 확인
4. `AI 자막` 탭에는 이후 구현 예정임을 명시

## 5. 빌드

```cmd
npm run build
```

생성된 `dist/`는 배포 산출물이며 GitHub에 올라가지 않습니다. MP4는 이후 AWS S3에 따로 업로드하여 CloudFront 경로로 제공할 계획입니다.

## 구현 범위

- 완료 목표: React OTT 홈, 콘텐츠 카드, MP4 재생, 파일 누락 오류 안내
- 미구현: 회원가입/로그인, Spring Boot/MySQL 연동, STT/LLM 번역, SRT/VTT 자막 선택, AWS 배포

영상 출처: Elephants Dream (2006), Blender Foundation, CC BY 2.5. 영상 시연 시 제작자와 라이선스 표시를 유지합니다.
