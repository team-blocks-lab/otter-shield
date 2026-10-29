import { useEffect, useState } from "react";
import { Link, navigate } from "../lib/router";
import { useAuth } from "../context/AuthContext";

export default function Header({ routeName }) {
  const { user, ready, logout } = useAuth();
  const [open, setOpen] = useState(false);
  const [solid, setSolid] = useState(false);
  useEffect(() => setOpen(false), [routeName]);
  useEffect(() => {
    const on = () => setSolid(window.scrollY > 24);
    on(); window.addEventListener("scroll", on, { passive: true });
    return () => window.removeEventListener("scroll", on);
  }, []);
  const onLogout = async () => { await logout(); navigate("/"); };
  const goLatest = (e) => { e.preventDefault(); navigate("/"); setTimeout(() => document.getElementById("rows")?.scrollIntoView({ behavior: "smooth" }), 60); };

  return (
    <header className={`nav${solid || routeName !== "home" ? " nav--solid" : ""}`}>
      <div className="container nav__row">
        <Link to="/" className="nav__logo" aria-label="OTTer 홈">OTTer<span className="nav__dot">.</span></Link>
        <button type="button" className="nav__toggle btn btn--ghost" aria-expanded={open} aria-controls="site-menu" onClick={() => setOpen((o) => !o)}>{open ? "닫기" : "메뉴"}</button>
        <nav id="site-menu" className={`nav__menu${open ? " is-open" : ""}`} aria-label="주 메뉴">
          <ul className="nav__links">
            <li><Link to="/" aria-current={routeName === "home" ? "page" : undefined}>홈</Link></li>
            <li><a href="#/" onClick={goLatest}>최신 영상</a></li>
          </ul>
          <div className="nav__auth">
            {!ready ? null : user ? (
              <><span className="nav__user">{user.name}님</span><button type="button" className="btn btn--ghost" onClick={onLogout}>로그아웃</button></>
            ) : (
              <><Link to="/login" className="btn btn--ghost">로그인</Link><Link to="/signup" className="btn btn--accent">회원가입</Link></>
            )}
          </div>
        </nav>
      </div>
    </header>
  );
}
