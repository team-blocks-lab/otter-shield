import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { api } from '../api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  // user: null(비로그인) 또는 { loginId }. 서버 세션 쿠키가 진실이며 브라우저 저장소에는 아무것도 쓰지 않습니다.
  const [user, setUser] = useState(null)
  const [checking, setChecking] = useState(true)

  useEffect(() => {
    let alive = true
    // 별도 "내 정보" API 명세가 없어 GET /api/cart 응답 여부로 로그인 세션을 확인합니다.
    api
      .getCart()
      .then(() => alive && setUser({ loginId: null }))
      .catch(() => alive && setUser(null))
      .finally(() => alive && setChecking(false))
    return () => {
      alive = false
    }
  }, [])

  const login = useCallback(async (loginId, password) => {
    await api.login({ loginId, password })
    setUser({ loginId })
  }, [])

  const signup = useCallback(({ name, loginId, password }) => api.signup({ name, loginId, password }), [])

  const logout = useCallback(async () => {
    try {
      await api.logout()
    } finally {
      setUser(null)
    }
  }, [])

  const value = useMemo(() => ({ user, checking, login, signup, logout }), [user, checking, login, signup, logout])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  return useContext(AuthContext)
}

