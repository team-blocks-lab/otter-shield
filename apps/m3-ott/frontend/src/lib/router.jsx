import { useEffect, useState, useCallback } from "react";

// S3 + CloudFront 정적 배포에서 서버 rewrite 설정 없이 새로고침이 동작하도록 해시 라우팅을 사용한다.
const ROUTES = [
  { name: "home", pattern: /^\/$/ },
  { name: "detail", pattern: /^\/videos\/([^/]+)$/, keys: ["id"] },
  { name: "watch", pattern: /^\/watch\/([^/]+)$/, keys: ["id"] },
  { name: "login", pattern: /^\/login$/ },
  { name: "signup", pattern: /^\/signup$/ },
];

function readPath() {
  const raw = window.location.hash.replace(/^#/, "");
  const [path, query = ""] = raw.split("?");
  return { path: path || "/", query: new URLSearchParams(query) };
}

export function matchRoute(path) {
  for (const r of ROUTES) {
    const m = path.match(r.pattern);
    if (m) {
      const params = {};
      (r.keys || []).forEach((k, i) => (params[k] = decodeURIComponent(m[i + 1])));
      return { name: r.name, params };
    }
  }
  return { name: "notfound", params: {} };
}

export function useRoute() {
  const [loc, setLoc] = useState(readPath);
  useEffect(() => {
    const on = () => setLoc(readPath());
    window.addEventListener("hashchange", on);
    return () => window.removeEventListener("hashchange", on);
  }, []);
  return { ...matchRoute(loc.path), path: loc.path, query: loc.query };
}

export function navigate(to) {
  window.location.hash = to;
}

export function Link({ to, children, ...rest }) {
  return (
    <a href={`#${to}`} {...rest}>
      {children}
    </a>
  );
}

export function useNavigate() {
  return useCallback((to) => navigate(to), []);
}
