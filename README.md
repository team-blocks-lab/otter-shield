# OTTer Shield

OTT 서비스 환경을 구축하고, 공격 재현 전후의 보안 설정과 서비스 동작을 비교하는 5인 팀 프로젝트.

- **팀:** Team Blocks
- **기간:** 2026.10.19–11.16 · 킥오프 10.16 · 최종 발표 11.16
- **진행 단계:** AS-IS 기본망 구축 및 망 간 연결 점검

가상 OTT 고객사 **OTTer**를 대상으로, 보안 컨설팅사 **NetBlocks**의 역할을 맡아 진행합니다. 본사·지사 업무망, OTT 서비스망, 백업 환경을 나누어 구축하고 각 구역의 취약점을 확인합니다. 개선 후에는 같은 공격을 다시 시도하고, 정상 서비스도 계속 이용할 수 있는지 확인합니다.

## 프로젝트 구성

| 구역 | 담당 | 주요 작업 |
|---|---|---|
| M1 한국 본사 | 유용수 | GNS3·UTM·부서망 구성, 사내 게시판 배포, VPN |
| M2 독일 본사 | 윤서영 | 본사와 같은 업무망 구성, 게시판 배포, Wazuh 탐지·대응 |
| M3 한국 서비스 | 홍은서 · PL | 가상화·Kubernetes·DB 구성, OTT·AI 서비스 배포, 인프라 통합 검토 |
| M4 백업·DR | 박진혁 | DB 복제·백업, S3 보관, 격리 환경 복원 |
| M5 캐나다 지사 | 유은혜 · PM | 지사망·VPN, 전체 앱 설계·개발, AI·AWS Store, 일정 관리 |

**앱 개발:** 유은혜 — 사내 게시판·OTT/AI·Store 설계, 구현, 자체 검증. 서버 배포와 DB 운영은 각 구역 담당자와 진행합니다.

한국·독일 업무망은 같은 게시판 코드를 사용하고 DB·계정·환경설정은 분리합니다. 독일망에는 Wazuh를 적용해 탐지·대응 결과를 비교할 계획입니다.

[망별 구성도](design/topology/README.md) · [ERD·클래스 다이어그램](design/README.md)

## 구축·보안 검증 계획

기본망 구축 → 정상 통신 확인 → 공격 재현·원인 분석 → 보안 설정 개선 → 재공격·서비스 확인

| 구역 | 확인할 내용 |
|---|---|
| M1 | 직원 PC의 서버 관리 접근 제한 |
| M2 | 반복 SSH 인증 실패 탐지 및 대응 방식 검증 |
| M3 | Kubernetes 관리 API 접근 제한, 허용된 관리자 접근 확인 |
| M4 | 백업 파일 접근 권한 개선 및 DB 복원 |
| M5 | VPN을 통한 한국 시험용 DB 직접 접근 제한, 웹 업무 접속 확인 |

현재는 AS-IS 구축 단계이며, 위 항목은 이후 검증 계획입니다. 구축 결과와 남은 작업은 [WBS](WBS.md), [업무 보드](https://github.com/orgs/team-blocks-lab/projects/4/views/2), 각 담당 기술서에서 관리합니다.

## 사용·도입 예정 기술

| 영역 | 기술 |
|---|---|
| 네트워크·가상화 | GNS3, VMware/vSphere, Sophos UTM 9, VyOS |
| 보안·컨테이너·클라우드 | Wazuh, Kubernetes, AWS |
| 앱 개발 | React, Spring Boot, FastAPI |
| 배포 자동화 | GitHub Actions, Argo CD |

## 프로젝트 자료

| 자료 | 링크 |
|---|---|
| 일정·업무 | [WBS](WBS.md) · [최종 Excel](https://raw.githubusercontent.com/team-blocks-lab/otter-shield/main/plans/wbs/otter-shield-wbs-final.xlsx) · [업무 보드](https://github.com/orgs/team-blocks-lab/projects/4/views/2) |
| 계획서 | [프로젝트 수행 계획서 v1.0](plans/project-plan-v1.0.pdf) |
| 설계 | [토폴로지·ERD·클래스 다이어그램](design/README.md) |
| 앱 | [사내 게시판](apps/m1-intranet/README.md) · [OTT·AI 자막](apps/m3-ott/README.md) · [캐나다 Store](apps/m5-store/README.md) |
| 회의 | [회의록·결정사항](meetings/README.md) |
| 협업 | [작업 기록·검토·완료 처리 방법](CONTRIBUTING.md) |

## 팀원별 기술서

장비 설정, 명령어, 장애 해결 과정과 통신 시험 결과 기록.

| 담당 | 작성 공간 |
|---|---|
| M1 유용수 | [한국 본사 기술서](https://app.notion.com/p/3fdddb1a63778183b7a1e9a9f0ed714e?source=copy_link) |
| M2 윤서영 | [독일 본사 기술서](https://www.notion.so/2-401432be815b8078abb2c707a8949dbd?source=copy_link) |
| M3 홍은서 | [서비스망 기술서](https://app.notion.com/p/M3-3feaf3d0fc69814dbacbfc2f49641045?source=copy_link) |
| M4 박진혁 | [백업·DR 기술서](https://app.notion.com/p/OTT-3fe63051315180188c80d52d09aa7bed?source=copy_link) |
| M5 유은혜 | [캐나다 지사 기술서](https://app.notion.com/p/05-Project-OTTer-Shield-M5-3ff1b198732a81c19379cd1f5730afbe?source=copy_link) |

### Issue 진행 댓글 작성

[댓글 양식과 작업 순서 → 팀 작업 안내](CONTRIBUTING.md#issue-진행-댓글-작성)
