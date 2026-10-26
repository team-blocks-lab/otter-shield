# OTTer Shield | WBS

Team Blocks · 최종 발표 2026.11.16 · **10/26 개정**

[WBS 엑셀](plans/wbs/otter-shield-wbs-final.xlsx) · [업무 보드](https://github.com/orgs/team-blocks-lab/projects/4/views/2) · [Topology](design/topology/README.md) · [회의록](meetings/README.md)

엑셀에서 일정과 선행 작업을 확인하고, 담당 Issue에서 세부 작업·완료 조건을 확인합니다.

## 주요 일정

| 기간 | 인프라·보안 | 개발·배포 |
|---|---|---|
| 10/27–28 | 망별 장비·서버 구성, DB·접속 정보 공유 | 공통 게시판 최소 기능 개발·10/28 실행본 인계 |
| 10/29–30 | 망 간 라우팅·VPN, M4 복제·백업·별도 복원, **10/30 AS-IS 완료** | M1/M2 게시판 배포, M3 OTT 최소 기능 배포·확인 |
| 10/31–11/01 | — | M5 Store 로컬 최소 기능·AWS 기반 구성 |
| 11/02 | S01~S05 공격 재현·원인 분석 | 배포 오류 보완 |
| 11/03 | TO-BE 설계·정책 검토 | Store 초기 연동·AI 자막 개발 |
| 11/04–06 | 보안 개선·독일 Wazuh·복구 시험·11/06 중간 통합 확인 | CI/CD 착수 시 도구 선정, 11/05 Store HTTPS·11/06 자막 파일 목표 |
| 11/07–10 | 재공격·정상 업무·장애 복구 검증 | 자막 재생·세션·롤백 보완, 11/09 앱 최종 통합·11/09–10 RDS 복원 |
| 11/11–12 | 망별 검증 결과·보고서 정리 | 발표자료 취합·기술 검토 |
| 11/13–14 | 리허설·대체 시연·파일 점검 | 발표본 확정 |
| **11/16** | **최종 발표** | 시연·실습 자원 정리 |

후속 개발·배포 날짜는 목표일입니다. 주말 작업은 담당자 가용시간을 확인하며, 10/28·10/30에 남은 작업과 연동 상태를 점검합니다.

## 담당별 Issue

| 담당 | AS-IS | 공격 | 개선 | 재검증 | 결과 정리 |
|---|---|---|---|---|---|
| M1 유용수 | [#14](https://github.com/team-blocks-lab/otter-shield/issues/14) | [#15](https://github.com/team-blocks-lab/otter-shield/issues/15) | [#16](https://github.com/team-blocks-lab/otter-shield/issues/16) | [#17](https://github.com/team-blocks-lab/otter-shield/issues/17) | [#18](https://github.com/team-blocks-lab/otter-shield/issues/18) |
| M2 윤서영 | [#19](https://github.com/team-blocks-lab/otter-shield/issues/19) | [#20](https://github.com/team-blocks-lab/otter-shield/issues/20) | [#21](https://github.com/team-blocks-lab/otter-shield/issues/21) | [#22](https://github.com/team-blocks-lab/otter-shield/issues/22) | [#23](https://github.com/team-blocks-lab/otter-shield/issues/23) |
| M3 홍은서 | [#24](https://github.com/team-blocks-lab/otter-shield/issues/24) | [#25](https://github.com/team-blocks-lab/otter-shield/issues/25) | [#26](https://github.com/team-blocks-lab/otter-shield/issues/26) | [#27](https://github.com/team-blocks-lab/otter-shield/issues/27) | [#28](https://github.com/team-blocks-lab/otter-shield/issues/28) |
| M4 박진혁 | [#29](https://github.com/team-blocks-lab/otter-shield/issues/29) | [#30](https://github.com/team-blocks-lab/otter-shield/issues/30) | [#31](https://github.com/team-blocks-lab/otter-shield/issues/31) | [#32](https://github.com/team-blocks-lab/otter-shield/issues/32) | [#33](https://github.com/team-blocks-lab/otter-shield/issues/33) |
| M5 유은혜 | [#34](https://github.com/team-blocks-lab/otter-shield/issues/34) | [#35](https://github.com/team-blocks-lab/otter-shield/issues/35) | [#36](https://github.com/team-blocks-lab/otter-shield/issues/36) | [#37](https://github.com/team-blocks-lab/otter-shield/issues/37) | [#38](https://github.com/team-blocks-lab/otter-shield/issues/38) |

## 공동 작업·인계

| 작업 | 담당·인계 순서 | Issue |
|---|---|---|
| 설계·일정 관리 | 각 담당 진행 공유 → PM 일정 조정·PL 연결 검토 | [#6](https://github.com/team-blocks-lab/otter-shield/issues/6) |
| 라우팅·VPN | M1 종단 도달성 확인 → M2/M5 VPN · M1–M3 일반 라우팅 | [#7](https://github.com/team-blocks-lab/otter-shield/issues/7) |
| 게시판·OTT·AI | M5 코드·DB 명세 인계 → M1/M2 게시판 배포 · M3 OTT/AI 배포 | [#8](https://github.com/team-blocks-lab/otter-shield/issues/8) |
| 독일 Wazuh | M2 AS-IS 인증 로그 확보 → Wazuh 탐지·대응·해제 확인 | [#9](https://github.com/team-blocks-lab/otter-shield/issues/9) |
| DB 복제·백업·복원 | M3 DB 접속 정보 인계 → M4 Replica·덤프·별도 복원·ProxySQL 검증 | [#10](https://github.com/team-blocks-lab/otter-shield/issues/10) |
| 앱·노드 장애 복구 | M3 클러스터·M5 앱/세션 준비 → 공동 장애·복구 시험 | [#11](https://github.com/team-blocks-lab/otter-shield/issues/11) |
| AWS Store | M5 계정·리전·권한 확인 → EKS/RDS·ALB·도메인·앱 연동 | [#12](https://github.com/team-blocks-lab/otter-shield/issues/12) |
| 최종 보고·발표 | 각 담당 결과·한계·시연 자료 제출 → PM 취합·PL 검토 | [#13](https://github.com/team-blocks-lab/otter-shield/issues/13) |

## 변경 이력

| 일자 | 변경 내용 | 사유·영향 |
|---|---|---|
| 10/26 | AS-IS 완료 10/28 → **10/30** | M3·M4 설계 변경과 구축 진척 반영. 공격 재현 목표 11/02로 조정 |
| 10/26 | M3 ESXi 내부 Linux VM에서 Kubernetes 운영 | 노드망 연결·클러스터·DB·앱 배포 순서 반영 |
| 10/26 | M4 **OPS·REPLICA·BACKUP·PROXY** 설계 확정 | Replica·ProxySQL·백업·별도 복원을 AS-IS에 포함 |
| 10/26 | CI/CD 도구 선정 보류 | 작업 착수 시 GitHub Actions + Argo CD와 Jenkins 기반 구성 비교·선정 |
| 10/26 | M5 개발·AWS 일정 재배치 | 10/30 게시판·OTT 최소 배포 우선. Store·AI·자동화는 순차 진행 |

완료한 작업과 기록은 유지합니다. 변경된 구간은 실제 동작을 다시 확인하고, 지연 시 받는 담당에게 영향과 예상 완료일을 공유합니다. 상세 설정·검증 기록은 팀 Notion 기술서에 남깁니다.
