import { USE_MOCK, IS_LOCAL_DEMO } from "../api";
export default function Footer() {
  return (
    <footer className="foot">
      <div className="container foot__row">
        <p className="foot__brand">OTTer<span className="nav__dot">.</span></p>
        <p className="foot__note">
          샘플 영상 Elephants Dream (2006), Blender Foundation, <a href="https://creativecommons.org/licenses/by/2.5/" target="_blank" rel="noopener noreferrer">CC BY 2.5</a>.
          {USE_MOCK ? " 현재 Mock 데이터로 동작 중입니다." : IS_LOCAL_DEMO ? " 재생은 개발용 로컬 샘플입니다." : ""}
        </p>
      </div>
    </footer>
  );
}
