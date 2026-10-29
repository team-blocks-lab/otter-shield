import { Link } from "../lib/router";
import { formatDuration } from "../lib/format";
import { genreLabel, toneOf } from "../lib/genre";
import Poster from "./Poster";
import { PlayIcon } from "./Icons";

export default function VideoCard({ video }) {
  return (
    <li className="card">
      <Link to={`/videos/${encodeURIComponent(video.id)}`} className="card__link" aria-label={`${video.title} 상세 보기`}>
        <Poster video={video} />
        <span className="card__shade" aria-hidden="true" />
        <span className="card__info">
          <span className="card__title">{video.title}</span>
          <span className="card__meta">
            {video.genre && <span className={`chip ${toneOf(video.genre)}`}>{genreLabel(video.genre)}</span>}
            <span className="num">{formatDuration(video.durationSec)}</span>
          </span>
          <span className="card__play"><PlayIcon width={16} height={16} /> 자세히</span>
        </span>
      </Link>
    </li>
  );
}
