import { useEffect, useState } from "react";
import { login, logout, getCurrentEmployee, initialSetup } from "./api";
import PostBoard from "./PostBoard";

const NAV_ITEMS = [
  { id: "ALL", symbol: "◈", label: "커뮤니티 피드" },
  { id: "PRAISE", symbol: "♡", label: "칭찬 게시판" },
  { id: "LUNCH", symbol: "◉", label: "점심 이야기" },
  { id: "MINE", symbol: "▣", label: "내 게시글" },
];

function Avatar({ name, small = false }) {
  return (
    <span
      className={`user-avatar${small ? " user-avatar--small" : ""}`}
      aria-hidden="true"
    >
      {(name || "직").charAt(0)}
    </span>
  );
}

export default function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [employeeNumber, setEmployeeNumber] = useState("");
  const [password, setPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [nickname, setNickname] = useState("");
  const [filter, setFilter] = useState("ALL");
  const [search, setSearch] = useState("");

  // 새로고침 후 서버 세션에 따른 로그인 상태 복원
  useEffect(() => {
    async function restoreSession() {
      try {
        setUser(await getCurrentEmployee());
      } catch (err) {
        if (err.status !== 401) setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    restoreSession();
  }, []);

  // 직원 로그인 처리
  async function handleLogin(event) {
    event.preventDefault();
    setError("");
    try {
      setUser(await login(employeeNumber, password));
      setPassword("");
    } catch (err) {
      setError(err.message);
    }
  }

  // 최초 로그인 시 비밀번호·닉네임 변경
  async function handleInitialSetup(event) {
    event.preventDefault();
    setError("");
    try {
      await initialSetup(newPassword, nickname);
      setUser(await getCurrentEmployee());
      setNewPassword("");
    } catch (err) {
      setError(err.message);
    }
  }

  // 서버 로그아웃 완료 후 사용자 상태 초기화
  async function handleLogout() {
    setError("");
    try {
      await logout();
      setUser(null);
      setEmployeeNumber("");
      setFilter("ALL");
      setSearch("");
    } catch (err) {
      setError(err.message);
    }
  }

  if (loading)
    return (
      <div className="auth-page">
        <p>로그인 상태 확인 중...</p>
      </div>
    );

  // 로그인 및 최초 설정 화면은 별도 카드로 구성
  if (!user || user.firstLogin) {
    return (
      <div className="auth-page">
        <div className="auth-card">
          <div className="brand-mark">O</div>
          <p className="eyebrow">OUR COMMUNITY</p>
          <h1>OTTer Inside</h1>
          <p className="auth-description">
            우리의 이야기를 나누는 따뜻한 사내 공간
          </p>
          {error && (
            <p className="status-error" role="alert">
              {error}
            </p>
          )}
          {!user ? (
            <form className="auth-form" onSubmit={handleLogin}>
              <h2>직원 로그인</h2>
              <label htmlFor="employeeNumber">직원 번호</label>
              <input
                id="employeeNumber"
                value={employeeNumber}
                onChange={(e) => setEmployeeNumber(e.target.value)}
                required
                autoComplete="username"
              />
              <label htmlFor="password">비밀번호</label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                autoComplete="current-password"
              />
              <button className="primary-btn" type="submit">
                로그인
              </button>
            </form>
          ) : (
            <form className="auth-form" onSubmit={handleInitialSetup}>
              <h2>최초 로그인 설정</h2>
              <p className="muted">비밀번호와 닉네임을 먼저 설정해 주세요.</p>
              <label htmlFor="newPassword">새 비밀번호</label>
              <input
                id="newPassword"
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                minLength={8}
                maxLength={64}
                required
                autoComplete="new-password"
              />
              <label htmlFor="nickname">닉네임</label>
              <input
                id="nickname"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                minLength={2}
                maxLength={30}
                required
              />
              <button className="primary-btn" type="submit">
                설정 완료
              </button>
            </form>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="app-shell">
      <aside className="side-navigation" aria-label="사내 커뮤니티 메뉴">
        <div className="brand-section">
          <div className="brand-name">
            OTTer Inside<span className="brand-dot">.</span>
          </div>
          <p>사내 커뮤니티</p>
        </div>
        <nav className="navigation-list" aria-label="게시판 종류">
          {NAV_ITEMS.map((item) => (
            <button
              key={item.id}
              type="button"
              className={`nav-link${filter === item.id ? " active" : ""}`}
              onClick={() => setFilter(item.id)}
              aria-current={filter === item.id ? "page" : undefined}
            >
              <span className="nav-symbol" aria-hidden="true">
                {item.symbol}
              </span>
              <span>{item.label}</span>
            </button>
          ))}
        </nav>
        <div className="sidebar-footer">
          <Avatar name={user.nickname} />
          <div>
            <strong>{user.nickname}</strong>
            <small>로그인 직원</small>
          </div>
        </div>
      </aside>

      <div className="workspace">
        <header className="topbar">
          <span className="breadcrumb">
            커뮤니티 <span>/</span> 전체 피드
          </span>
          <div className="topbar-right">
            <label className="search-field">
              <span aria-hidden="true">⌕</span>
              <span className="sr-only">현재 페이지 게시글 검색</span>
              <input
                type="search"
                placeholder="현재 페이지 검색"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </label>
            <Avatar name={user.nickname} small />
            <button type="button" className="text-btn" onClick={handleLogout}>
              로그아웃
            </button>
          </div>
        </header>

        <div className="content-grid">
          <main className="feed-main">
            <div className="hero-heading">
              <span className="eyebrow">OUR COMMUNITY</span>
              <h1>우리의 오늘을 공유해요</h1>
              <p>칭찬과 점심 이야기, 함께 만드는 사내 커뮤니티</p>
            </div>
            {error && (
              <p className="status-error" role="alert">
                {error}
              </p>
            )}
            <PostBoard
              nickname={user.nickname}
              filter={filter}
              onFilterChange={setFilter}
              search={search}
            />
          </main>

          <aside className="right-panel" aria-label="커뮤니티 이용 안내">
            <div className="right-welcome">
              <h2>안녕하세요, {user.nickname}님!</h2>
              <p>오늘도 팀의 이야기를 만나보세요.</p>
            </div>
            <div className="guide-card">
              <span className="guide-kicker">WELCOME TO OTTer</span>
              <h3>커뮤니티 이용 안내</h3>
              <div className="guide-item">
                <span>01</span> 점심 메뉴와 사진 공유
              </div>
              <div className="guide-item">
                <span>02</span> 칭찬 게시글만 익명 작성 가능
              </div>
              <div className="guide-item">
                <span>03</span> 내 게시글 수정·삭제
              </div>
            </div>
            <p className="side-note">
              이 공간은 로그인한 직원만 이용할 수 있습니다. 좋아요·댓글은 추후
              기능으로 준비 중입니다.
            </p>
          </aside>
        </div>
      </div>
    </div>
  );
}
