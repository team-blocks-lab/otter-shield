# OTTer Shield | 개발 코드 안내

팀명 Team Blocks · 보안 컨설팅 회사 NetBlocks · 가상 OTT 고객사 OTTer

프로젝트명: OTTer Shield — 한국·캐나다 OTT 하이브리드 인프라 구축 및 보안 컨설팅

세 웹 서비스의 코드를 배포 대상별로 구분합니다. 모든 앱 코드 개발은 M5 유은혜가 담당하며, M1·M3·M5 표기는 개발자가 아닌 배포 대상입니다.

| 서비스 | 폴더 | 포함할 코드 | 배포 담당·범위 |
|---|---|---|---|
| M1·M2 사내 게시판 | [m1-intranet](m1-intranet/README.md) | 한국·독일 공통 게시판 앱 | M1 한국·M2 독일 배포, M5 개발·연동 지원 |
| M3 OTT 메인 | [m3-ott](m3-ott/README.md) | React·Spring Boot·FastAPI/AI | M5 React·API 개발, M3 OTT React 웹·API·DB 배포, M5 OTT 영상 S3/CloudFront CDN |
| M5 Store | [m5-store](m5-store/README.md) | Store React·Spring Boot | M5 캐나다 AWS/EKS 배포 |

게시판 코드는 기존 `m1-intranet` 폴더를 M1·M2가 공유합니다. 독일용 앱 사본은 만들지 않으며, 국가별 DB·계정·환경변수·DNS를 분리합니다. 폴더 이름의 M1은 기존 경로를 유지한 것입니다.

## 내려받은 뒤 확인할 위치

1. 저장소 첫 화면의 Code → Download ZIP으로 전체 파일을 내려받습니다.
2. 압축 해제 후 `otter-shield-main/apps` 폴더에서 배포할 서비스 폴더를 선택합니다.
3. 그 서비스의 README에서 필요한 구성 요소와 실행 방법을 확인합니다.
4. M1 사내 게시판과 M3 OTT의 실행 코드는 등록됐습니다. 서비스별 구현·배포 검증 상태는 해당 README에서 확인합니다. M3 재생/자막 변경은 현재 `feat/m3-ott` 브랜치에 있으며 main 반영은 별도 확인해야 합니다.

OTT의 `frontend/`, `backend/`, `ai-service/`는 `apps/m3-ott/` 아래에서 확인합니다.

## 코드 등록·배포 시 기록할 내용

서비스별 README에 실제 버전, 실행·빌드 명령, 환경변수 이름, DB 준비, 검증된 커밋·이미지 버전을 기록합니다. 비밀값은 등록하지 않습니다.

**팀 최신 결정: M3 OTT 배포 자동화는 Jenkins 기준으로 진행합니다.** M3 홍은서가 OTT React 웹과 Spring Boot API·DB의 배포를 담당합니다. OTT React 정적 웹의 실제 배치 위치(정적 호스팅·웹 서버 등)는 M3 인프라 구성에 맞춰 확정하고, API의 Kubernetes 배포와 작업을 구분합니다. M5 유은혜는 OTT React·API 코드를 개발·인계하고, 별도로 캐나다 Store AWS 배포 및 OTT MP4 영상의 S3 업로드·CloudFront CDN 연동을 담당합니다. Jenkinsfile·Job·배포 검증은 아직 완료되지 않았으며, Store 등 다른 서비스의 Jenkins 전환 범위는 별도 합의 사항입니다. 서비스별 배포 경로를 분리해 OTT와 Store가 서로 잘못 배포되지 않도록 검증합니다. M1·M2 게시판의 배포 방식은 해당 앱 환경을 확인한 뒤 정합니다.

## 인계 기준

실제 버전·빌드/실행 명령·DB 스키마/가짜 초기 데이터·환경변수 이름·건강 확인 경로·커밋/이미지·되돌리기 절차를 서비스 README에 채운 뒤 상대 배포 담당이 확인합니다. 10/23까지 게시판 실행 방식·이미지 저장소·Git/이미지 출구·승인 관리 경로를 결정합니다.

[프로젝트 첫 화면](../README.md)
