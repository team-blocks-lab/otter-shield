import { useEffect, useRef, useState, useCallback } from "react";
import { formatClock } from "../lib/format";
import { PlayIcon, PauseIcon, VolumeIcon, MutedIcon, CaptionIcon, FullIcon } from "./Icons";

const LANG_LABEL = { ko: "한국어", en: "English" };
const LOAD_TIMEOUT_MS = 15000;

// videoUrl(string|null), subtitles([{lang,url}]) — 재생 API 응답을 그대로 받는다. 자막은 WebVTT.
export default function Player({ videoUrl, subtitles = [], title }) {
  const wrapRef = useRef(null);
  const vref = useRef(null);
  const [phase, setPhase] = useState(videoUrl ? "loading" : "missing"); // loading | ready | error | missing
  const [playing, setPlaying] = useState(false);
  const [time, setTime] = useState(0);
  const [dur, setDur] = useState(0);
  const [vol, setVol] = useState(1);
  const [muted, setMuted] = useState(false);
  const [full, setFull] = useState(false);
  const [capMenu, setCapMenu] = useState(false);
  const [cap, setCap] = useState("off");

  useEffect(() => {
    if (phase !== "loading") return;
    const t = setTimeout(() => setPhase((p) => (p === "loading" ? "error" : p)), LOAD_TIMEOUT_MS);
    return () => clearTimeout(t);
  }, [phase]);
  useEffect(() => {
    const on = () => setFull(document.fullscreenElement === wrapRef.current);
    document.addEventListener("fullscreenchange", on);
    return () => document.removeEventListener("fullscreenchange", on);
  }, []);
  useEffect(() => {
    const v = vref.current;
    if (!v) return;
    for (const t of Array.from(v.textTracks)) t.mode = t.language === cap ? "showing" : "disabled";
  }, [cap, phase, subtitles]);

  const toggle = useCallback(() => {
    const v = vref.current;
    if (!v || phase !== "ready") return;
    if (v.paused) v.play().catch(() => {}); else v.pause();
  }, [phase]);
  const seek = (e) => { const v = vref.current; if (v) { v.currentTime = Number(e.target.value); setTime(v.currentTime); } };
  const setVolume = (e) => { const v = vref.current; const n = Number(e.target.value); v.volume = n; v.muted = n === 0; setVol(n); setMuted(n === 0); };
  const toggleMute = () => { const v = vref.current; v.muted = !v.muted; setMuted(v.muted); };
  const toggleFull = () => { if (document.fullscreenElement) document.exitFullscreen(); else wrapRef.current?.requestFullscreen?.().catch(() => {}); };
  const onKey = (e) => {
    if (e.target.closest("input, [role='menu']")) return;
    const v = vref.current;
    if (e.key === " " || e.key === "k") { e.preventDefault(); toggle(); }
    else if (e.key === "ArrowRight" && v) v.currentTime = Math.min(dur, v.currentTime + 5);
    else if (e.key === "ArrowLeft" && v) v.currentTime = Math.max(0, v.currentTime - 5);
    else if (e.key === "f") toggleFull();
    else if (e.key === "m") toggleMute();
  };

  if (phase === "missing") {
    return (<div className="player player--empty" role="status"><h2 className="state__title">재생할 영상 파일이 없습니다</h2><p>이 항목에는 영상 파일이 연결되어 있지 않습니다.</p></div>);
  }
  if (phase === "error") {
    return (
      <div className="player player--empty" role="alert">
        <h2 className="state__title">영상을 재생할 수 없습니다</h2>
        <p>영상 파일을 불러오지 못했습니다. <code>frontend/public/media/elephants-dream.mp4</code> 파일 위치와 네트워크를 확인한 뒤 다시 시도해 주세요.</p>
        <button type="button" className="btn btn--primary" onClick={() => setPhase("loading")}>다시 시도</button>
      </div>
    );
  }
  const pct = dur ? (time / dur) * 100 : 0;

  return (
    <div className="player" ref={wrapRef} onKeyDown={onKey} aria-label={`${title} 플레이어`}>
      <video key={videoUrl} ref={vref} className="player__video" src={videoUrl} preload="metadata" playsInline crossOrigin="anonymous"
        onClick={toggle}
        onLoadedMetadata={(e) => { setDur(e.currentTarget.duration); setPhase("ready"); }}
        onTimeUpdate={(e) => setTime(e.currentTarget.currentTime)}
        onPlay={() => setPlaying(true)} onPause={() => setPlaying(false)} onEnded={() => setPlaying(false)}
        onError={() => setPhase("error")}>
        {subtitles.map((s) => <track key={s.lang} kind="subtitles" srcLang={s.lang} label={LANG_LABEL[s.lang] || s.lang} src={s.url} />)}
      </video>

      {phase === "loading" && <div className="player__overlay" role="status"><span className="state__bar" aria-hidden="true" /><p>영상을 불러오는 중</p></div>}
      {phase === "ready" && !playing && (
        <button type="button" className="player__big" onClick={toggle} aria-label="재생"><PlayIcon width={36} height={36} /></button>
      )}

      <div className="controls" hidden={phase !== "ready"}>
        <input className="range range--seek" type="range" min="0" max={dur || 0} step="0.1" value={time} onChange={seek}
          style={{ "--p": `${pct}%` }} aria-label="재생 위치" aria-valuetext={`${formatClock(time)} / ${formatClock(dur)}`} />
        <div className="controls__row">
          <button type="button" className="icon-btn" onClick={toggle} aria-label={playing ? "일시정지" : "재생"}>{playing ? <PauseIcon /> : <PlayIcon />}</button>
          <span className="controls__time num">{formatClock(time)} / {formatClock(dur)}</span>
          <span className="controls__spacer" />
          <button type="button" className="icon-btn" onClick={toggleMute} aria-label={muted ? "음소거 해제" : "음소거"} aria-pressed={muted}>{muted ? <MutedIcon /> : <VolumeIcon />}</button>
          <input className="range range--vol" type="range" min="0" max="1" step="0.05" value={muted ? 0 : vol} onChange={setVolume} style={{ "--p": `${(muted ? 0 : vol) * 100}%` }} aria-label="볼륨" />
          <div className="controls__cap">
            <button type="button" className="icon-btn icon-btn--text" aria-haspopup="menu" aria-expanded={capMenu} onClick={() => setCapMenu((o) => !o)}>
              <CaptionIcon /> <span>{cap === "off" ? "자막 끔" : LANG_LABEL[cap]}</span>
            </button>
            {capMenu && (
              <ul className="menu" role="menu">
                {subtitles.length === 0 ? <li className="menu__none" role="none">이 영상에는 자막이 없습니다.</li> : (
                  <>
                    <li role="none"><button role="menuitemradio" aria-checked={cap === "off"} type="button" onClick={() => { setCap("off"); setCapMenu(false); }}>자막 끄기</button></li>
                    {subtitles.map((s) => <li role="none" key={s.lang}><button role="menuitemradio" aria-checked={cap === s.lang} type="button" onClick={() => { setCap(s.lang); setCapMenu(false); }}>{LANG_LABEL[s.lang] || s.lang}</button></li>)}
                  </>
                )}
              </ul>
            )}
          </div>
          <button type="button" className="icon-btn" onClick={toggleFull} aria-label={full ? "전체화면 해제" : "전체화면"} aria-pressed={full}><FullIcon /></button>
        </div>
      </div>
    </div>
  );
}
