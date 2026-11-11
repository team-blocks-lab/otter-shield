# 팀원별 보안 검증 기록

[프로젝트 첫 화면](README.md) · [망별 설계](design/topology/README.md) · [서비스 시연](apps/README.md)

아래 링크는 각 담당자의 작업 및 검증 기록으로 연결합니다. 이슈 제목이나 일정만으로 검증 성공을 판단하지 않습니다.

| 담당 | 비교할 항목 | 기본 구축 | 개선 전 | 설정 변경 | 재검증 |
|---|---|---|---|---|---|
| **M1 유용수** | 직원망의 관리망 접근 및 정상 업무 통신 | [#14](https://github.com/team-blocks-lab/otter-shield/issues/14) | [#15](https://github.com/team-blocks-lab/otter-shield/issues/15) | [#16](https://github.com/team-blocks-lab/otter-shield/issues/16) | [#17](https://github.com/team-blocks-lab/otter-shield/issues/17) |
| **M2 윤서영** | 반복 SSH 인증 실패의 로그 수집·탐지 및 대응 | [#19](https://github.com/team-blocks-lab/otter-shield/issues/19) | [#20](https://github.com/team-blocks-lab/otter-shield/issues/20) | [#21](https://github.com/team-blocks-lab/otter-shield/issues/21) | [#22](https://github.com/team-blocks-lab/otter-shield/issues/22) |
| **M3 홍은서 · PL** | Kubernetes 관리 API 접근 제한 및 허용 관리자 접근 | [#24](https://github.com/team-blocks-lab/otter-shield/issues/24) | [#25](https://github.com/team-blocks-lab/otter-shield/issues/25) | [#26](https://github.com/team-blocks-lab/otter-shield/issues/26) | [#27](https://github.com/team-blocks-lab/otter-shield/issues/27) |
| **M4 박진혁** | 백업 파일 접근 제한 및 별도 DB 복원 | [#29](https://github.com/team-blocks-lab/otter-shield/issues/29) | [#30](https://github.com/team-blocks-lab/otter-shield/issues/30) | [#31](https://github.com/team-blocks-lab/otter-shield/issues/31) | [#32](https://github.com/team-blocks-lab/otter-shield/issues/32) |
| **M5 유은혜 · PM** | VPN 경유 시험용 DB 직접 접근 제한 및 정상 웹 업무 접속 | [#34](https://github.com/team-blocks-lab/otter-shield/issues/34) | [#35](https://github.com/team-blocks-lab/otter-shield/issues/35) | [#36](https://github.com/team-blocks-lab/otter-shield/issues/36) | [#37](https://github.com/team-blocks-lab/otter-shield/issues/37) |

## 발표와 포트폴리오에 남길 결과

각 담당 이슈 또는 기술서에서 다음 네 가지를 짝으로 연결합니다.

1. **개선 전:** 시험 출발지와 대상, 실제 관찰 결과.
2. **변경 내용:** 적용한 정책과 적용 위치.
3. **개선 후:** 같은 조건에서 다시 확인한 결과.
4. **정상 동작:** 허용된 사용자나 서비스가 계속 동작하는지 확인한 증거.

실행 로그·화면·기술서 링크가 확인된 항목에만 검증 결과를 붙입니다. 앱 기능 검증과 AWS 배포 증거는 [개발 문서](apps/README.md)에 기록합니다.
