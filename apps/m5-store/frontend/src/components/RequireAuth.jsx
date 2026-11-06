import { useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Loading, LoginRequired } from './States'

export default function RequireAuth({ children }) {
  const { user, checking } = useAuth()
  const location = useLocation()
  if (checking) return <Loading label="로그인 상태를 확인하는 중입니다" />
  if (!user) {
    return (
      <div className="container page">
        <LoginRequired from={location.pathname} />
      </div>
    )
  }
  return children
}
