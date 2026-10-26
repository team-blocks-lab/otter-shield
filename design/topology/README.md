# Topology

담당망별 장비·네트워크 구역과 정상 서비스, 관리·백업·보안 시험 경로를 나타냅니다.

| 담당망 | Topology | 주요 구성과 기능 |
|---|---|---|
| M1 한국 본사 | [AS-IS Topology](M1_%ED%95%9C%EA%B5%AD%20%EB%B3%B8%EC%82%AC_AS-IS.drawio.png) | 부서 VLAN·공통 업무 서버·UTM·VPN 연결 |
| M2 독일 본사 | [AS-IS Topology](M2_%EB%8F%85%EC%9D%BC%20%EB%B3%B8%EC%82%AC_AS-IS.drawio.png) | 독립 본사 업무망·게시판·독일 Wazuh 개선 계획 |
| M3 한국 OTT 서비스망 | [AS-IS Topology](M3_%ED%95%9C%EA%B5%AD%20%EC%84%9C%EB%B9%84%EC%8A%A4%EB%A7%9D_AS-IS.drawio.png) | ESXi·Kubernetes·OTT/AI 배포·DB·서비스 진입 |
| M4 백업·DR망 | [AS-IS Topology](M4%20%EB%B0%B1%EC%97%85%C2%B7DR%EB%A7%9D.png) | DB 백업·복제·S3 사본 보관·별도 복원 |
| M5 캐나다·AWS Store | [AS-IS Topology](M5_%EC%BA%90%EB%82%98%EB%8B%A4%EB%A7%9D_AS-IS.drawio.png) | 캐나다 직원망·한국 VPN·AWS Store 공개 서비스 |

TO-BE Topology는 공격 분석 후 개선한 접근 정책과 추가 구성을 반영합니다.

[설계 자료](../README.md)
