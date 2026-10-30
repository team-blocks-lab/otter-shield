import { createContext, useContext, useEffect, useState, useCallback, useMemo } from "react";
import { api } from "../api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [ready, setReady] = useState(false);

  // 새로고침 후 세션 복원: 서버(me)에 물어보는 구조. 401이면 비로그인으로 둔다.
  useEffect(() => {
    const ac = new AbortController();
    api.auth.me(ac.signal).then(setUser).catch(() => setUser(null)).finally(() => setReady(true));
    return () => ac.abort();
  }, []);

  const login = useCallback(async (input) => setUser(await api.auth.login(input)), []);
  const signup = useCallback((input) => api.auth.signup(input), []);
  const logout = useCallback(async () => {
    try { await api.auth.logout(); } finally { setUser(null); }
  }, []);

  const value = useMemo(() => ({ user, ready, login, signup, logout }), [user, ready, login, signup, logout]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
