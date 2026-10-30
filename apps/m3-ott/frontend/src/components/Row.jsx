import { useRef } from "react";
import VideoCard from "./VideoCard";
import { ChevronIcon } from "./Icons";

// 가로 스크롤 캐러셀. 마우스 휠/터치/키보드 Tab 이동과 이전·다음 버튼을 모두 지원한다.
export default function Row({ id, title, items, onMore }) {
  const ref = useRef(null);
  const page = (dir) => {
    const el = ref.current;
    el?.scrollBy({ left: dir * el.clientWidth * 0.85, behavior: matchMedia("(prefers-reduced-motion: reduce)").matches ? "auto" : "smooth" });
  };
  return (
    <section className="row" aria-labelledby={id}>
      <div className="container row__head">
        <h2 id={id} className="row__title">{title}</h2>
      </div>
      <div className="row__stage">
        <button type="button" className="row__nav row__nav--prev" onClick={() => page(-1)} aria-label={`${title} 이전`}><ChevronIcon dir="left" /></button>
        <ul className="row__track" ref={ref}>
          {items.map((v) => <VideoCard key={v.id} video={v} />)}
          {onMore && (
            <li className="card card--more"><button type="button" className="btn btn--ghost" onClick={onMore}>더 보기</button></li>
          )}
        </ul>
        <button type="button" className="row__nav row__nav--next" onClick={() => page(1)} aria-label={`${title} 다음`}><ChevronIcon /></button>
      </div>
    </section>
  );
}
