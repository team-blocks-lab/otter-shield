# OTTer Shield

**한국·독일 본사, 한국 OTT 서비스망, 백업·DR, 캐나다 AWS를 연결하는 5인 하이브리드 인프라 프로젝트**

Team Blocks · 킥오프 2026.10.16 · 최종 발표 2026.11.16

[팀원별 기여](#팀원별-기여) · [망별 설계](design/topology/README.md) · [서비스 시연·개발 증거](apps/README.md) · [보안 검증 기록](VERIFICATION.md)

![OTTer Shield 전체망 AS-IS 구성도](design/topology/M_%EC%A0%84%EC%B2%B4%EB%A7%9D_AS-IS.drawio.png)

*AS-IS: 보안 개선 전 기준 설계.*

## 팀원별 기여

| 담당 | 구역 | 담당 범위 | 기록 |
|---|---|---|---|
| **M1 유용수** | 한국 본사 | 부서 VLAN·라우팅·VPN, 관리망 접근 정책 | 구축 [#14](https://github.com/team-blocks-lab/otter-shield/issues/14) · [기술서](https://app.notion.com/p/3fdddb1a63778183b7a1e9a9f0ed714e?source=copy_link) |
| **M2 윤서영** | 독일 본사 | 업무망·게시판 배포, Wazuh 인증 로그 탐지 | 구축 [#19](https://github.com/team-blocks-lab/otter-shield/issues/19) · [기술서](https://www.notion.so/2-401432be815b8078abb2c707a8949dbd?source=copy_link) |
| **M3 홍은서 · PL** | 한국 OTT | vSphere·Kubernetes, 웹·API·DB 배포 및 통합 검토 | 구축 [#24](https://github.com/team-blocks-lab/otter-shield/issues/24) · [기술서](https://app.notion.com/p/M3-3feaf3d0fc69814dbacbfc2f49641045?source=copy_link) |
| **M4 박진혁** | 백업·DR | DB 복제·백업 보관·별도 환경 복원 | 구축 [#29](https://github.com/team-blocks-lab/otter-shield/issues/29) · [기술서](https://app.notion.com/p/2-3fe63051315180789253dc5752bc0e6d?source=copy_link) |
| **M5 유은혜 · PM** | 캐나다 지사·AWS | 지사망·VPN, 앱 개발, Store EKS 배포 자동화 | 구축 [#34](https://github.com/team-blocks-lab/otter-shield/issues/34) · [기술서](https://app.notion.com/p/05-Project-OTTer-Shield-M5-3ff1b198732a81c19379cd1f5730afbe?source=copy_link) |

## 확인된 서비스 결과

| 서비스 | 확인한 결과 | 증거 |
|---|---|---|
| OTTer Store | 공개 HTTPS에서 상품 조회부터 모의 주문 완료까지 확인. PR 머지로 시작한 이미지 빌드·배포 설정 갱신과 EKS 롤아웃 확인 | [시연·자동 배포](apps/README.md#otter-store) |
| OTTer OTT | 서비스 화면에서 영상 재생과 한국어·영어 자막 표시 확인 | [재생 화면·API 검증](apps/README.md#otter-ott) |
| OTTer Inside | 개발 PC에서 로그인·게시글·첨부 API와 화면 확인 | [게시판 화면·API 검증](apps/README.md#사내-게시판--otter-inside) |

## 보안 개선 전후

격리 환경에서 개선 전 동작을 기록하고, 설정 변경 후 같은 조건에서 접근 차단과 정상 서비스 유지 여부를 비교합니다. [구역별 검증 기록](VERIFICATION.md)에서 시나리오와 이슈를 확인할 수 있습니다.

## 프로젝트 자료

| 구분 | 자료 |
|---|---|
| 설계 | [토폴로지·ERD·클래스 다이어그램](design/README.md) |
| 일정·업무 | [WBS](WBS.md) · [엑셀](plans/wbs/otter-shield-wbs-final.xlsx) · [팀 작업 보드](https://github.com/orgs/team-blocks-lab/projects/4/views/2) |
| 팀 운영 | [수행 계획서 v1.0](plans/project-plan-v1.0.pdf) · [회의록](meetings/README.md) · [팀 작업 안내](CONTRIBUTING.md) |
