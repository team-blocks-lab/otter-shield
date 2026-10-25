# OTTer Shield

한국·캐나다 OTT 하이브리드 인프라 구축 및 보안 컨설팅

Team Blocks가 보안 컨설팅 회사 NetBlocks의 역할로 가상 OTT 고객사 OTTer의 인프라를 구축하고, 취약점 진단·개선·재검증을 수행하는 5인 팀 프로젝트입니다.

프로젝트 운영: 2026.10.19–11.16 · 킥오프: 10.16 · 최종 발표: 11.16 · 현재 단계: AS-IS 구축·통합 기술 검토

한국·독일 온프레미스 본사, 한국 OTT 서비스, 백업·DR, 캐나다 지사와 AWS Store를 구성합니다. 한국·독일은 같은 업무 구성을 기반으로 독일 전용 Wazuh의 탐지·대응 효과를 비교합니다.

## 프로젝트 자료

- 업무·일정: [WBS](WBS.md) · [최종 엑셀 다운로드](https://raw.githubusercontent.com/team-blocks-lab/otter-shield/main/plans/wbs/otter-shield-wbs-final.xlsx) · [업무 보드](https://github.com/orgs/team-blocks-lab/projects/4/views/2)
- 최종 계획서: [프로젝트 수행 계획서 v1.0 PDF](plans/project-plan-v1.0.pdf)
- 회의·결정사항: [회의록](meetings/README.md) 
- 팀 설계 자료: [Topology · ERD · Class Diagram](design/README.md)
- 개발 코드: [Intranet](apps/m1-intranet/README.md) · [OTT & AI](apps/m3-ott/README.md) · [Store](apps/m5-store/README.md)
- 구축·검증 기록: [팀원별 Notion 기술서](#팀원별-기술서)

Intranet은 한국·독일 사내 게시판, OTT & AI는 OTT 서비스·AI 자막, Store는 캐나다 쇼핑 서비스를 뜻합니다.

계획서 v0.3은 변경 전 계획 이력입니다. PDF의 M2 관제·상위 장비·일부 공격 경로/관리 중계 설명은 현재 구축 지시로 사용하지 않습니다. 실행 기준은 WBS·10/20 회의록·팀 검토 토폴로지·각 Issue입니다. 개발 폴더는 현재 실행 코드 등록 전입니다.

## 팀 구성 및 담당

| 구역 | 담당 | 책임 영역 | 시작 Issue |
|---|---|---|---|
| M1 한국 본사 | 유용수 | GNS3·UTM·부서망·게시판 배포·VPN | [#1](https://github.com/team-blocks-lab/otter-shield/issues/1) |
| M2 독일 본사 | 윤서영 | M1과 같은 업무망·게시판 배포·독일 전용 Wazuh | [#2](https://github.com/team-blocks-lab/otter-shield/issues/2) |
| M3 한국 서비스 | 홍은서 · PL | 가상화·Kubernetes·DB·OTT/AI 배포·인프라 통합 검토 | [#3](https://github.com/team-blocks-lab/otter-shield/issues/3) |
| M4 백업·DR | 박진혁 | DB 복제·백업·S3·격리 복원 | [#4](https://github.com/team-blocks-lab/otter-shield/issues/4) |
| M5 캐나다 | 유은혜 · PM | 지사망·VPN·전체 앱 코드·AI·AWS Store·프로젝트 관리 | [#5](https://github.com/team-blocks-lab/otter-shield/issues/5) |

개발 설계(ERD·클래스·API)·코드 구현·자체 검증은 PM(M5) 유은혜가 단독으로 진행합니다. PL은 개발 검토를 담당하지 않으며, 각 서버 담당은 배포·DB 운영·인프라 연결을 확인합니다. M1·M2는 같은 게시판 코드를 사용하되 DB·계정·환경설정은 분리합니다. M1 후속 실장비 간소화 설계는 주담당 윤서영 / 보조 박진혁입니다.

## 구현·검증 방향

AS-IS 설계 → 구축·정상 업무 확인 → 공격·원인 분석 → TO-BE 설계·검토 → 구현 → 재시험·정상 업무 확인

| 구역 | 검증 주제 |
|---|---|
| M1 | 직원의 서버 관리 접근을 UTM으로 제한 |
| M2 | 반복 SSH 인증 실패를 Wazuh로 탐지하고 검토 후 선택한 자동 대응 방식 검증 |
| M3 | Kubernetes 관리 API 노출 제한·승인 관리자 접근 유지 |
| M4 | 백업 파일 접근 권한 개선·실제 DB 복원 |
| M5 | 업무 VPN을 통한 한국 격리 Test DB 직접 접근 제한·웹 업무 유지 |

주요 도입 기술: GNS3, VMware/vSphere, UTM(Sophos UTM 9), VyOS(M3), Wazuh(M2 전용), Kubernetes, AWS, React, Spring Boot, FastAPI, GitHub Actions, Argo CD. 정확한 버전·실제 할당 자원·시험 결과는 구축 시 기술서에 기록합니다.

## 현재 진행

10/21 IP 배정·M1–M3 일반 라우팅 결정·WBS/계획서 제출을 완료했습니다. 현재 AS-IS 구축을 진행하며, 전체 망 기술 검토와 일정 조정은 [#6](https://github.com/team-blocks-lab/otter-shield/issues/6)에서 관리합니다.

## 팀원별 기술서

설정·명령어·오류 해결·시험 증거는 각 담당의 Notion에 기록합니다. 아래는 팀원이 공유한 작성 공간이며 제출 완료를 뜻하지 않습니다. Notion 접근 권한은 해당 작성자에게 요청합니다.

| 담당 | 작성 공간 |
|---|---|
| M1 유용수 | [한국 본사 기술서](https://app.notion.com/p/3fdddb1a63778183b7a1e9a9f0ed714e?source=copy_link) |
| M2 윤서영 | [독일 본사 기술서](https://www.notion.so/2-401432be815b8078abb2c707a8949dbd?source=copy_link) |
| M3 홍은서 | [서비스망 기술서](https://app.notion.com/p/M3-3feaf3d0fc69814dbacbfc2f49641045?source=copy_link) |
| M4 박진혁 | [백업·DR 기술서](https://app.notion.com/p/OTT-3fe63051315180188c80d52d09aa7bed?source=copy_link) |
| M5 유은혜 | [캐나다 지사 기술서](https://app.notion.com/p/05-Project-OTTer-Shield-M5-3ff1b198732a81c19379cd1f5730afbe?source=copy_link) |

새 문서가 필요한 경우 [공통 작성 양식](https://app.notion.com/p/3fd1b198732a81cf9c44fb58dfcb8567?pvs=204)을 복제합니다. 양식과 실제 작성 문서를 구분합니다.

## 팀원 작업 순서

1. [WBS](WBS.md)에서 이번 작업과 기한을 확인하고 연결된 Issue를 엽니다.
2. 본인 Notion 기술서에 실제 설정·명령어·오류·검증 결과를 기록합니다.
3. Issue 댓글에 오늘 한 일 / 증거 링크 / 막힌 점 / 다음 작업을 남깁니다.
4. 업무 보드의 진행 상태를 갱신하고, 검토 후 완료 처리합니다.

### Issue 진행 댓글 작성

작업한 날 마무리 전에 해당 Issue 하단의 댓글 입력란에 아래 내용을 작성하고 **Comment**를 누릅니다. 여러 Issue를 진행했다면 각각 기록합니다. 작업을 막는 문제가 생기면 종료 시간까지 기다리지 말고 즉시 기록합니다.

```text
작업일: YYYY-MM-DD
한 일:
확인 결과: 성공 / 일부 성공 / 실패 / 미확인 중 실제 결과와 이유
기록 링크: 본인 Notion의 해당 작업 위치 또는 결과 파일
막힌 점: 없으면 없음, 있으면 필요한 지원·일정 영향
다음 작업:
```

작성 예시(실제 실적 아님): “대표 PC에서 DNS 조회 성공. VPN은 반환 경로 미확인으로 보류. Notion 시험 기록 링크 첨부. 다음 작업은 상대 망 담당과 경로 대조.”

완료한 항목만 체크하고 작업 중에는 In progress, 진행 불가는 Blocked, 검토 요청은 In review로 표시합니다. 기술 결과는 관련 망 담당이 확인하고 망 간 변경은 PL이 함께 검토합니다. PM은 기한·누락·결과 링크를 확인합니다. 완료 기준과 검토를 충족한 뒤 Done으로 변경하고 Issue를 닫습니다. 댓글을 썼다는 이유만으로 완료 처리하지 않습니다.

WBS = 일정 · Projects = 진행 상태 · Issue = 작업·결과 · Notion = 기술 상세·증거

토폴로지는 팀 작성본을 등록합니다. 공개 저장소에는 비밀번호·API 키·VPN 비밀키·실제 내부 주소표를 올리지 않습니다.
