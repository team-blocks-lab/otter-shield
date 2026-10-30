// MOCK 데이터(실제 서버 데이터 아님). 실제 DTO(VideoSummaryResponse/VideoDetailResponse)를 정규화한 모양과 같다.
// elephants-dream 만 실제 샘플 영상(Blender Foundation, CC BY 2.5). "디자인용 샘플"은 레이아웃 확인용 자리표시이며 실제 작품이 아니다.
const sample = (n, genre) => ({
  id: `mock-${n}`, title: `디자인용 샘플 ${String(n).padStart(2, "0")}`, genre,
  durationSec: 300 + n * 47, thumbnailUrl: null, actor: "",
  summary: "레이아웃 확인을 위한 Mock 항목입니다. 영상 파일과 자막이 연결되어 있지 않습니다.",
  createdAt: `2026-10-${String(28 - n).padStart(2, "0")}T09:00:00Z`, visibility: "PUBLIC",
});
export const MOCK_VIDEOS = [
  { id: "elephants-dream", title: "Elephants Dream", genre: "ANIMATION", durationSec: 653, thumbnailUrl: "/media/elephants-dream.jpg", actor: "",
    summary: "Blender Foundation이 2006년에 공개한 오픈 무비입니다. Creative Commons Attribution 2.5 라이선스로 배포됩니다.",
    createdAt: "2026-10-28T09:00:00Z", visibility: "PUBLIC" },
  sample(2, "ANIMATION"), sample(3, "LIFE"), sample(4, "NATURE"), sample(5, "ANIMATION"), sample(6, "LIFE"), sample(7, "NATURE"), sample(8, "ANIMATION"),
  { ...sample(9, null), title: "비공개 샘플", visibility: "PRIVATE", createdAt: "2026-10-29T01:00:00Z" },
];
// 개발 확인용 계정(실제 인증 아님). 서버는 BCrypt 해시를 저장한다.
export const MOCK_MEMBERS = [{ id: 1, loginId: "demo01", name: "데모 사용자", password: "demo1234" }];
