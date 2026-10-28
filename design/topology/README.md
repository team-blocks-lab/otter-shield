# Topology

전체망과 담당망별 장비·네트워크 구역, 정상 서비스·관리·백업·보안 시험 경로를 나타냅니다. 현재 설계는 `main` 브랜치의 AS-IS 파일을 기준으로 확인합니다.

| 담당망 | Topology | 주요 구성과 기능 |
|---|---|---|
| 전체망 | [AS-IS Topology](M_%EC%A0%84%EC%B2%B4%EB%A7%9D_AS-IS.drawio.png?v=a5a679f47f7f3331) | M1~M5 연결·M4 내부 백업/복원·M3 미디어·M5 AWS Store |
| M1 한국 본사 | [AS-IS Topology](M1_%ED%95%9C%EA%B5%AD%20%EB%B3%B8%EC%82%AC_AS-IS.drawio.png) | 부서 VLAN·공통 업무 서버·UTM·VPN 연결 |
| M2 독일 본사 | [AS-IS Topology](M2_%EB%8F%85%EC%9D%BC%20%EB%B3%B8%EC%82%AC_AS-IS.drawio.png) | 독립 본사 업무망·게시판·독일 Wazuh 개선 계획 |
| M3 한국 OTT 서비스망 | [AS-IS Topology](M3_%ED%95%9C%EA%B5%AD%20%EC%84%9C%EB%B9%84%EC%8A%A4%EB%A7%9D_AS-IS.drawio.png) | ESXi·Kubernetes·OTT/AI 배포·DB·서비스 진입 |
| M4 백업·DR망 | [AS-IS Topology](M4%20%EB%B0%B1%EC%97%85%C2%B7DR%EB%A7%9D.png) | Replica·ProxySQL·M4 내부 백업 서버·별도 DB 복원 |
| M5 캐나다·AWS Store | [AS-IS Topology](M5_%EC%BA%90%EB%82%98%EB%8B%A4%EB%A7%9D_AS-IS.drawio.png) | 캐나다 직원망·한국 VPN·AWS Store 공개 서비스 |

TO-BE Topology는 공격 분석 후 개선한 접근 정책과 추가 구성을 반영합니다.

## PNG 교체·확인 방법

1. 로컬에서 수정한 도면을 PNG로 다시 내보내고, 파일을 열어 수정 내용이 반영됐는지 확인합니다.
2. GitHub에서 `main` 브랜치의 `design/topology` 폴더를 엽니다.
3. 기존 파일과 **동일한 파일명**으로 `Add file → Upload files`에서 업로드합니다. 기존 파일을 먼저 삭제할 필요는 없습니다.
4. 커밋 대상 브랜치를 확인하고 변경을 저장합니다. 다른 브랜치에 올렸다면 `main`에 병합한 뒤 확인합니다.
5. 저장 후 폴더로 돌아가 `main`의 파일을 열고, 필요하면 `Ctrl + Shift + R`로 새로고침합니다. 계속 이전 그림이 보이면 파일의 `History`에서 업로드 커밋을 열어 해당 버전을 확인합니다.
6. README의 이미지 링크에서 이전 그림이 보이면 링크 뒤 `?v=` 값을 새 교체 일시로 변경합니다. 예: `?v=20261028-0934`. 파일명을 변경했다면 참조하는 README 링크도 함께 수정합니다.

삭제와 업로드를 별도 커밋으로 나눠도 과거 커밋의 파일은 유지됩니다. 커밋 화면은 그 시점의 버전이므로, 현재 파일은 `main`에서 확인합니다.

**2026-10-28 교체 확인:** [09:34 업로드 커밋](https://github.com/team-blocks-lab/otter-shield/blob/fd60a544de529d7712d7a9ac88c05101d0774c5c/design/topology/M_%EC%A0%84%EC%B2%B4%EB%A7%9D_AS-IS.drawio.png) · [해당 PNG 원본](https://raw.githubusercontent.com/team-blocks-lab/otter-shield/fd60a544de529d7712d7a9ac88c05101d0774c5c/design/topology/M_%EC%A0%84%EC%B2%B4%EB%A7%9D_AS-IS.drawio.png)

[설계 자료](../README.md)
