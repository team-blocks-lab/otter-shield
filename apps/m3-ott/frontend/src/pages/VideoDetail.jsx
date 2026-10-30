import { useState } from "react";
import { api } from "../api";
import { useAsync } from "../lib/useAsync";
import { useAuth } from "../context/AuthContext";
import { Link, navigate } from "../lib/router";
import { formatDuration } from "../lib/format";
import { genreLabel, toneOf } from "../lib/genre";
import Poster from "../components/Poster";
import Modal from "../components/Modal";
import { PlayIcon } from "../components/Icons";
import { LoadingState, ErrorState } from "../components/StateViews";

export default function VideoDetail({ id }) {
  const { user } = useAuth();
  const { status, data: v, error, reload } = useAsync((s) => api.videos.get(id, s), [id]);
  const [ask, setAsk] = useState(false);

  if (status === "loading") return <div className="container page-pad"><LoadingState label="영상 정보를 불러오는 중" /></div>;
  if (status === "error") {
    return (
      <div className="container page-pad">
        <ErrorState message={error.status === 404 ? "영상을 찾을 수 없거나 공개되지 않은 영상입니다." : error.message} onRetry={error.status === 404 ? undefined : reload} />
        <p><Link to="/">홈으로 돌아가기</Link></p>
      </div>
    );
  }
  const onPlay = () => (user ? navigate(`/watch/${encodeURIComponent(v.id)}`) : setAsk(true));

  return (
    <article className={`detail ${toneOf(v.id)}`}>
      <div className="detail__bg" aria-hidden="true">{v.thumbnailUrl && <img src={v.thumbnailUrl} alt="" />}</div>
      <div className="container detail__grid">
        <div className="detail__card"><Poster video={v} /></div>
        <div className="detail__body">
          <p className="hero__tags">
            {v.genre && <span className={`chip ${toneOf(v.genre)}`}>{genreLabel(v.genre)}</span>}
            <span className="chip chip--plain num">{formatDuration(v.durationSec)}</span>
          </p>
          <h1 className="detail__title">{v.title}</h1>
          <p className="lead">{v.summary}</p>
          {v.actor && <dl className="facts"><div><dt>출연</dt><dd>{v.actor}</dd></div></dl>}
          <div className="hero__actions">
            <button type="button" className="btn btn--primary btn--lg" onClick={onPlay}><PlayIcon /> 재생</button>
            <Link to="/" className="btn btn--glass btn--lg">목록으로</Link>
          </div>
          {!user && <p className="hint">영상을 재생하려면 로그인이 필요합니다.</p>}
        </div>
      </div>
      {ask && (
        <Modal title="로그인이 필요합니다" onClose={() => setAsk(false)}>
          <p>목록과 상세 정보는 누구나 볼 수 있고, 재생은 로그인 후 가능합니다.</p>
          <div className="modal__actions">
            <Link to="/login" className="btn btn--primary">로그인</Link>
            <Link to="/signup" className="btn btn--accent">회원가입</Link>
            <button type="button" className="btn btn--ghost" onClick={() => setAsk(false)}>닫기</button>
          </div>
        </Modal>
      )}
    </article>
  );
}
