import { useState } from "react";
import { toneOf } from "../lib/genre";

// 썸네일 슬롯. 이미지가 없거나 로드에 실패하면 팔레트 색 면 + 제목으로 대체한다.
export default function Poster({ video, ratio = "poster", className = "" }) {
  const [broken, setBroken] = useState(false);
  const show = video.thumbnailUrl && !broken;
  return (
    <div className={`poster poster--${ratio} ${toneOf(video.id)} ${className}`}>
      {show ? <img src={video.thumbnailUrl} alt="" loading="lazy" onError={() => setBroken(true)} /> : <span className="poster__ph" aria-hidden="true">{video.title}</span>}
    </div>
  );
}
