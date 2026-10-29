import { useEffect, useRef } from "react";
import { useRoute, Link } from "./lib/router";
import { AuthProvider } from "./context/AuthContext";
import { USE_MOCK } from "./api";
import Header from "./components/Header";
import Footer from "./components/Footer";
import Home from "./pages/Home";
import VideoDetail from "./pages/VideoDetail";
import Watch from "./pages/Watch";
import AuthForm from "./pages/AuthForm";

const TITLES = { home: "OTTer", detail: "영상 상세 OTTer", watch: "재생 OTTer", login: "로그인 OTTer", signup: "회원가입 OTTer", notfound: "페이지 없음 OTTer" };

function Routes({ route }) {
  switch (route.name) {
    case "home": return <Home />;
    case "detail": return <VideoDetail id={route.params.id} key={route.params.id} />;
    case "watch": return <Watch id={route.params.id} key={route.params.id} />;
    case "login": return <AuthForm mode="login" key="login" />;
    case "signup": return <AuthForm mode="signup" key="signup" />;
    default: return <div className="container state"><h1 className="state__title">페이지를 찾을 수 없습니다</h1><p><Link to="/">홈으로</Link></p></div>;
  }
}

export default function App() {
  const route = useRoute();
  const main = useRef(null);
  useEffect(() => {
    document.title = TITLES[route.name] || "OTTer";
    window.scrollTo(0, 0);
    main.current?.focus({ preventScroll: true });
  }, [route.path]);

  return (
    <AuthProvider>
      <a href="#main" className="skip" onClick={(e) => { e.preventDefault(); main.current?.focus(); }}>본문으로 건너뛰기</a>
      {USE_MOCK && <p className="devbar" role="note">Mock 모드입니다. 서버 인증과 저장은 동작하지 않습니다.</p>}
      <Header routeName={route.name} />
      <main id="main" tabIndex={-1} ref={main}>
        <Routes route={route} />
      </main>
      <Footer />
    </AuthProvider>
  );
}
