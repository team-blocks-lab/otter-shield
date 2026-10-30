import { api } from "../api";
import { useAsync } from "../lib/useAsync";
import { useAuth } from "../context/AuthContext";
import { Link } from "../lib/router";
import Player from "../components/Player";
import { LoadingState, ErrorState } from "../components/StateViews";

export default function Watch({ id }) {
  const { user, ready } = useAuth();
  const meta = useAsync((s) => api.videos.get(id, s), [id]);
  // 재생 URL·자막 주소는 로그인 확인을 거친 재생 API에서만 받는다. 최종 판단은 서버(401/403/404).
  const play = useAsync((s) => (user ? api.videos.playback(id, s) : Promise.reject(Object.assign(new Error("로그인이 필요합니다."), { status: 401 }))), [id, user?.id]);

  if (!ready || meta.status === "loading" || play.status === "loading") return <div className="container page-pad"><LoadingState label="재생 준비 중" /></div>;
  if (!user || play.error?.status === 401) {
    return (
      <div className="container page-pad">
        <ErrorState message="재생은 로그인한 회원만 할 수 있습니다." />
        <p className="state__links"><Link to="/login" className="btn btn--primary">로그인</Link></p>
      </div>
    );
  }
  if (meta.status === "error" || play.status === "error") {
    const e = meta.error || play.error;
    const gone = e.status === 403 || e.status === 404;
    return (
      <div className="container page-pad">
        <ErrorState message={gone ? "접근할 수 없는 영상입니다." : e.message} onRetry={gone ? undefined : () => { meta.reload(); play.reload(); }} />
        <p><Link to="/">홈으로 돌아가기</Link></p>
      </div>
    );
  }
  return (
    <div className="container watch">
      <Player videoUrl={play.data.videoUrl} subtitles={play.data.subtitles || []} title={meta.data.title} />
      <div className="watch__info">
        <h1 className="watch__title">{meta.data.title}</h1>
        <Link to={`/videos/${encodeURIComponent(id)}`}>상세 정보</Link>
      </div>
      {play.data.localDemo && <p className="hint">개발용 로컬 샘플 재생입니다. 서버의 재생 권한 검증을 거친 영상이 아닙니다.</p>}
    </div>
  );
}
