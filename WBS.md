# OTTer Shield | WBS

Team Blocks · 2026.10.19–11.16 · 최종 발표 11/16

**[최종 WBS 엑셀 다운로드](https://raw.githubusercontent.com/team-blocks-lab/otter-shield/main/plans/wbs/otter-shield-wbs-final.xlsx)** · A3 가로 2장

[업무 보드](https://github.com/orgs/team-blocks-lab/projects/4/views/2) · [Topology](design/topology/README.md) · [팀 기술서](README.md#팀원별-기술서)

엑셀에서 담당 업무·날짜를 확인하고, 아래 Issue에서 세부 작업과 완료 기준을 확인합니다. 진행 상태와 결과는 Issue·업무 보드에 기록합니다.

## 주요 일정

| 기간 | 인프라·보안 | 개발·공동 작업 |
|---|---|---|
| 10/20–21 | AS-IS Topology 작성·등록 | WBS·계획서 제출 |
| 10/21–23 | 기본망 구축 착수·IP/자원 기술 검토 | 실행 환경·ERD·클래스 설계·게시판 개발, 10/23 공수 확인 |
| 10/24·26–28 | 기본 서비스·라우팅·VPN 구성, 10/28 기본망 목표 | OTT 로그인·권한·목록·영상 재생, CI 준비 점검 |
| 10/29–30 | M1·M2·M4 공격 재현·원인 분석 | 게시판·OTT 배포, Store 최소 기능·AWS 준비 점검 |
| 10/31 | — | Store AWS 최초 배포·연동 |
| 11/02 | M3·M5 공격 재현·원인 분석 | AI 개발·Store 잔여 오류 확인 |
| 11/03 | TO-BE Topology·보안 정책 설계 | AI 자막 파일 생성 |
| 11/04–05 | 보안 개선·이중화·DB 복제 구성 | 한국 CD·AI 배포 11/04, Store CD 11/05 |
| 11/06 | 개선 설정 통합·복원·장애 테스트 | 앱·AI·CI/CD 통합 테스트 |
| 11/09–10 | 재공격·정상 동작·장애 복구 검증 | 앱 회귀·배포 롤백·RDS 복원 검증 |
| 11/11–12 | 검증 결과·보고서·발표자료 정리 | PM 취합·PL 기술 검토 |
| 11/13–14 | 11/13 전원 리허설 | 11/14 파일·링크·대체 시연 점검 |
| 11/16 | 최종 발표 | 시연·실습 자원 정리 |

10/21 IP 배정·M1–M3 일반 라우팅 결정·WBS/계획서 제출은 완료했습니다. Topology 등록과 전체 망 기술 검토는 구분하며, 잔여 검토는 [#6](https://github.com/team-blocks-lab/otter-shield/issues/6)에서 관리합니다.

## 담당별 Issue

| 담당 | AS-IS 구축 | 공격 재현 | TO-BE 개선 | 재검증 | 결과 정리 | 개발·AWS |
|---|---|---|---|---|---|---|
| M1 유용수 · 한국 본사 | [#14](https://github.com/team-blocks-lab/otter-shield/issues/14) | [#15](https://github.com/team-blocks-lab/otter-shield/issues/15) | [#16](https://github.com/team-blocks-lab/otter-shield/issues/16) | [#17](https://github.com/team-blocks-lab/otter-shield/issues/17) | [#18](https://github.com/team-blocks-lab/otter-shield/issues/18) | — |
| M2 윤서영 · 독일 본사 | [#19](https://github.com/team-blocks-lab/otter-shield/issues/19) | [#20](https://github.com/team-blocks-lab/otter-shield/issues/20) | [#21](https://github.com/team-blocks-lab/otter-shield/issues/21) | [#22](https://github.com/team-blocks-lab/otter-shield/issues/22) | [#23](https://github.com/team-blocks-lab/otter-shield/issues/23) | — |
| M3 홍은서 · 한국 서비스·PL | [#24](https://github.com/team-blocks-lab/otter-shield/issues/24) | [#25](https://github.com/team-blocks-lab/otter-shield/issues/25) | [#26](https://github.com/team-blocks-lab/otter-shield/issues/26) | [#27](https://github.com/team-blocks-lab/otter-shield/issues/27) | [#28](https://github.com/team-blocks-lab/otter-shield/issues/28) | — |
| M4 박진혁 · 백업·DR | [#29](https://github.com/team-blocks-lab/otter-shield/issues/29) | [#30](https://github.com/team-blocks-lab/otter-shield/issues/30) | [#31](https://github.com/team-blocks-lab/otter-shield/issues/31) | [#32](https://github.com/team-blocks-lab/otter-shield/issues/32) | [#33](https://github.com/team-blocks-lab/otter-shield/issues/33) | — |
| M5 유은혜 · 캐나다·PM | [#34](https://github.com/team-blocks-lab/otter-shield/issues/34) | [#35](https://github.com/team-blocks-lab/otter-shield/issues/35) | [#36](https://github.com/team-blocks-lab/otter-shield/issues/36) | [#37](https://github.com/team-blocks-lab/otter-shield/issues/37) | [#38](https://github.com/team-blocks-lab/otter-shield/issues/38) | 앱 [#8](https://github.com/team-blocks-lab/otter-shield/issues/8) · Store·AWS [#12](https://github.com/team-blocks-lab/otter-shield/issues/12) |

## 개발·공동 작업

아래 날짜는 계획 일정입니다. **굵은 날짜는 주요 확인·마감일**이며, 실제 완료 여부와 인계 조건은 각 Issue에서 확인합니다.

| 작업 | 담당 | 일정·주요 기한 | Issue |
|---|---|---|---|
| 인프라 기술 검토·일정 조정 | PM·PL·각 망 담당 | 10/21–10/23<br>**10/23 자원·권한·남은 공수 확인** | [#6](https://github.com/team-blocks-lab/otter-shield/issues/6) |
| 망 간 라우팅·VPN | M1 주관·전원 | 10/22–23 대표 경로<br>**10/28 전체 연결·서비스 왕복 확인** | [#7](https://github.com/team-blocks-lab/otter-shield/issues/7) |
| 게시판·OTT·AI·CI/CD | M5 단독 설계·개발·자체 검증<br>배포: M1/M2 게시판·M3 OTT/AI·M5 Store | 10/21–11/06 개발·배포<br>**10/23 게시판 · 10/28 OTT · 10/30 Store 최소 기능**<br>10/29–30 게시판·OTT 배포 · 10/31 Store 최초 연동<br>11/03 AI 자막 · 11/04 한국 CD·AI 배포 · 11/05 Store CD<br>**11/06 통합 · 11/09–10 회귀·롤백** | [#8](https://github.com/team-blocks-lab/otter-shield/issues/8) |
| 독일 Wazuh | M2 | 10/26–28 준비 · 11/03 설계<br>11/04–05 구성·검증 · **11/06 통합 확인** | [#9](https://github.com/team-blocks-lab/otter-shield/issues/9) |
| DB 복제·백업·S3 복원 | M4·M3 | **10/23 복구 목표 합의** · 10/26–30 준비<br>11/03 설계 · 11/04 첫 격리 복원 · 11/05 초기 복제<br>11/06 S3 복원 · **11/09–10 복제·복구 검증** | [#10](https://github.com/team-blocks-lab/otter-shield/issues/10) |
| 앱·노드·VIP 장애 복구 | M3·M5 | 11/02–03 시험 준비<br>**11/06·11/09–10 장애·복구 시험** | [#11](https://github.com/team-blocks-lab/otter-shield/issues/11) |
| AWS·Store·RDS 복원 | M5 주관·M3/M1 협업 | **10/23 권한·예산·API 경로 · 10/30 AWS 준비**<br>10/31 Store 최초 연동 · 11/05 Store CD · 11/06 통합<br>**11/09–10 RDS 격리 복원** | [#12](https://github.com/team-blocks-lab/otter-shield/issues/12) |
| 최종 보고·발표 | 전원, PM 취합·PL 검토 | 11/09–16<br>**11/11 증거 취합 · 11/12 발표자료 · 11/13 리허설**<br>11/14 파일·링크 점검 · **11/16 발표·자원 정리** | [#13](https://github.com/team-blocks-lab/otter-shield/issues/13) |

세부 인계 조건은 각 Issue에 기록합니다. 지연이 예상되면 영향·예상 완료일을 남기고 PM·PL과 조정한 뒤 엑셀과 WBS를 함께 갱신합니다. 실제 설정·로그·시험 증거는 Notion 기술서에 남깁니다.
