import { useRef } from "react";
import { Link } from "../lib/router";
import { formatDuration } from "../lib/format";
import { genreLabel, toneOf } from "../lib/genre";
import Poster from "./Poster";
import { PlayIcon, InfoIcon } from "./Icons";

// 3겹 레이어(배경 / 장면 / 떠 있는 포스터)가 포인터에 따라 서로 다르게 움직여 깊이를 만든다. 모션 감소 설정에서는 CSS가 끈다.
export default function Hero({ video }) {
  const ref = useRef(null);
  const onMove = (e) => {
    const r = ref.current.getBoundingClientRect();
    ref.current.style.setProperty("--px", ((e.clientX - r.left) / r.width - 0.5).toFixed(3));
    ref.current.style.setProperty("--py", ((e.clientY - r.top) / r.height - 0.5).toFixed(3));
  };
  const reset = () => { ref.current.style.setProperty("--px", 0); ref.current.style.setProperty("--py", 0); };
  const watch = `/watch/${encodeURIComponent(video.id)}`;
  const detail = `/videos/${encodeURIComponent(video.id)}`;
  const first = video.summary.split(/(?<=[.!?。])\s/)[0];

  return (
    <section className={`hero ${toneOf(video.id)}`} ref={ref} onPointerMove={onMove} onPointerLeave={reset} aria-labelledby="hero-title">
      <div className="hero__bg" aria-hidden="true">{video.thumbnailUrl && <img src={video.thumbnailUrl} alt="" />}</div>
      <div className="hero__scrim" aria-hidden="true" />
      <div className="container hero__grid">
        <div className="hero__text">
          <p className="hero__tags">
            {video.genre && <span className={`chip ${toneOf(video.genre)}`}>{genreLabel(video.genre)}</span>}
            <span className="chip chip--plain num">{formatDuration(video.durationSec)}</span>
          </p>
          <h1 id="hero-title" className="hero__title">{video.title}</h1>
          {first && <p className="hero__lead">{first}</p>}
          <div className="hero__actions">
            <Link to={watch} className="btn btn--primary btn--lg"><PlayIcon /> 재생</Link>
            <Link to={detail} className="btn btn--glass btn--lg"><InfoIcon /> 상세 정보</Link>
          </div>
        </div>
        <Link to={detail} className="hero__card" tabIndex={-1} aria-hidden="true"><Poster video={video} /></Link>
      </div>
    </section>
  );
}
