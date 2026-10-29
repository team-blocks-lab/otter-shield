const LABEL = { ANIMATION: "애니메이션", NATURE: "자연", LIFE: "일상", ACTION: "액션", FANTASY: "판타지", ROMANCE: "로맨스", COMEDY: "코미디" };
export const genreLabel = (g) => (g ? LABEL[g] || g : "");
// 장르와 영상 id를 팔레트 6색 중 하나로 안정적으로 매핑한다 (CSS .tone-0 ~ .tone-5)
const hash = (s) => [...String(s)].reduce((a, c) => (a * 31 + c.charCodeAt(0)) >>> 0, 7);
export const toneOf = (s) => `tone-${hash(s || "x") % 6}`;
