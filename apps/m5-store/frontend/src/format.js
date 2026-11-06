const won = new Intl.NumberFormat("ko-KR");
export const formatWon = (value) => `${won.format(Number(value) || 0)}원`;

export function formatDate(value) {
  if (!value) return "";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return String(value);
  return new Intl.DateTimeFormat("ko-KR", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(d);
}

// 서버가 주는 imageKey는 "/images/goods/mug.jpg" 형태. 없으면 null → 로컬 SVG 사용.
export function imageUrl(imageKey) {
  if (!imageKey) return null;
  if (/^https?:\/\//.test(imageKey) || imageKey.startsWith("/"))
    return imageKey;
  return `/${imageKey}`;
}

export const PLACEHOLDER = "/placeholder.svg";
