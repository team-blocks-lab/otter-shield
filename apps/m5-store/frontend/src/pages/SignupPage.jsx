import { useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function SignupPage() {
  const { signup } = useAuth()
  const navigate = useNavigate()
  const [loginId, setLoginId] = useState('')
  const [password, setPassword] = useState('')
  const [confirm, setConfirm] = useState('')
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [pending, setPending] = useState(false)
  const busy = useRef(false)

  async function handleSubmit(event) {
    event.preventDefault()
    if (busy.current) return
    if (!loginId.trim() || !password) return setError('아이디와 비밀번호를 입력해 주세요.')
    if (password !== confirm) return setError('비밀번호 확인이 일치하지 않습니다.')
    busy.current = true
    setPending(true)
    setError('')
    setFieldErrors({})
    try {
      await signup(loginId.trim(), password)
      navigate('/login', { replace: true, state: { registered: true } })
    } catch (e) {
      setError(e.message)
      setFieldErrors(e.fieldErrors || {})
    } finally {
      busy.current = false
      setPending(false)
    }
  }

  return (
    <div className="container page page-narrow">
      <h1 className="page-title">회원가입</h1>
      <p className="muted">OTTer Store 전용 계정입니다. OTT 서비스 계정과 연동되지 않습니다.</p>
      <form className="form" onSubmit={handleSubmit} noValidate>
        <label className="field">
          <span>아이디</span>
          <input value={loginId} onChange={(e) => setLoginId(e.target.value)} autoComplete="username" required />
          <small className="field-error">{fieldErrors.loginId}</small>
        </label>
        <label className="field">
          <span>비밀번호</span>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} autoComplete="new-password" required />
          <small className="field-error">{fieldErrors.password}</small>
        </label>
        <label className="field">
          <span>비밀번호 확인</span>
          <input type="password" value={confirm} onChange={(e) => setConfirm(e.target.value)} autoComplete="new-password" required />
          <small className="field-error" />
        </label>
        <p className="form-error" role="alert">
          {error}
        </p>
        <button className="btn btn-primary btn-block" disabled={pending}>
          {pending ? '가입 중' : '가입하기'}
        </button>
      </form>
      <p className="form-alt">
        이미 계정이 있나요? <Link to="/login">로그인</Link>
      </p>
    </div>
  )
}
