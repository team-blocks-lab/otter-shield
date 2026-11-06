import { useRef, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = location.state?.from || '/'
  const notice = location.state?.notice || location.state?.registered
  const [loginId, setLoginId] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [pending, setPending] = useState(false)
  const busy = useRef(false)

  async function handleSubmit(event) {
    event.preventDefault()
    if (busy.current) return
    if (!loginId.trim() || !password) {
      setError('아이디와 비밀번호를 입력해 주세요.')
      return
    }
    busy.current = true
    setPending(true)
    setError('')
    try {
      await login(loginId.trim(), password)
      setPassword('')
      navigate(from, { replace: true })
    } catch (e) {
      setError(e.message)
    } finally {
      busy.current = false
      setPending(false)
    }
  }

  return (
    <div className="container page page-narrow">
      <h1 className="page-title">로그인</h1>
      {notice && <p className="notice">{typeof notice === 'string' ? notice : '회원가입이 완료되었습니다. 로그인해 주세요.'}</p>}
      <form className="form" onSubmit={handleSubmit} noValidate>
        <label className="field">
          <span>아이디</span>
          <input value={loginId} onChange={(e) => setLoginId(e.target.value)} autoComplete="username" required />
        </label>
        <label className="field">
          <span>비밀번호</span>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} autoComplete="current-password" required />
        </label>
        <p className="form-error" role="alert">
          {error}
        </p>
        <button className="btn btn-primary btn-block" disabled={pending}>
          {pending ? '로그인 중' : '로그인'}
        </button>
      </form>
      <p className="form-alt">
        아직 계정이 없나요? <Link to="/signup">회원가입</Link>
      </p>
    </div>
  )
}
