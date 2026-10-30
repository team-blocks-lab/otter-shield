import { useEffect, useMemo, useState } from "react";
import { api } from "../api";
import { genreLabel, toneOf } from "../lib/genre";
import Hero from "../components/Hero";
import Row from "../components/Row";
import { LoadingState, ErrorState, EmptyState } from "../components/StateViews";

const SIZE = 24;

export default function Home() {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const [status, setStatus] = useState("loading");
  const [error, setError] = useState(null);
  const [genre, setGenre] = useState("ALL");

  const load = (p) => {
    if (p === 0) setStatus("loading");
    api.videos.list({ page: p, size: SIZE })
      .then((r) => { setItems((prev) => (p === 0 ? r.items : [...prev, ...r.items])); setPage(p); setHasNext(r.hasNext); setStatus("success"); })
      .catch((e) => { setError(e); setStatus("error"); });
  };
  useEffect(() => { load(0); }, []);

  const genres = useMemo(() => [...new Set(items.map((v) => v.genre).filter(Boolean))], [items]);
  const more = hasNext ? () => load(page + 1) : undefined;

  if (status === "loading") return <div className="container page-pad"><LoadingState label="영상 목록을 불러오는 중" /></div>;
  if (status === "error") return <div className="container page-pad"><ErrorState message={error?.message} onRetry={() => load(0)} /></div>;
  if (items.length === 0) return <div className="container page-pad"><EmptyState title="아직 공개된 영상이 없습니다">새 영상이 등록되면 이곳에 표시됩니다.</EmptyState></div>;

  return (
    <>
      <Hero video={items[0]} />
      <div id="rows" className="rows">
        {genres.length > 0 && (
          <div className="container filters" role="group" aria-label="장르 선택">
            <button type="button" className="filter" aria-pressed={genre === "ALL"} onClick={() => setGenre("ALL")}>전체</button>
            {genres.map((g) => (
              <button type="button" key={g} className={`filter ${toneOf(g)}`} aria-pressed={genre === g} onClick={() => setGenre(g)}>{genreLabel(g)}</button>
            ))}
          </div>
        )}
        {genre === "ALL" ? (
          <>
            <Row id="row-latest" title="최신 영상" items={items} onMore={more} />
            {genres.length > 1 && genres.map((g) => <Row key={g} id={`row-${g}`} title={genreLabel(g)} items={items.filter((v) => v.genre === g)} />)}
          </>
        ) : (
          <Row id="row-genre" title={genreLabel(genre)} items={items.filter((v) => v.genre === genre)} onMore={more} />
        )}
      </div>
    </>
  );
}
