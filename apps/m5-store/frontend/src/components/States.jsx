import { Link } from 'react-router-dom'

export function Loading({ label = '불러오는 중입니다' }) {
  return (
    <div className="state" role="status" aria-live="polite">
      <span className="spinner" aria-hidden="true" />
      <p>{label}</p>
    </div>
  )
}

export function Empty({ title, children, action }) {
  return (
    <div className="state">
      <h2 className="state-title">{title}</h2>
      {children && <p>{children}</p>}
      {action}
    </div>
  )
}

export function ErrorBox({ error, onRetry, title = '정보를 불러오지 못했습니다' }) {
  return (
    <div className="state state-error" role="alert">
      <h2 className="state-title">{title}</h2>
      <p>{error?.message || '알 수 없는 오류가 발생했습니다.'}</p>
      {onRetry && (
        <button type="button" className="btn btn-secondary" onClick={onRetry}>
          다시 시도
        </button>
      )}
    </div>
  )
}

export function LoginRequired({ from = '/', message = '이 화면은 로그인 후 이용할 수 있습니다.' }) {
  return (
    <div className="state">
      <h2 className="state-title">로그인이 필요합니다</h2>
      <p>{message}</p>
      <div className="row">
        <Link className="btn btn-primary" to="/login" state={{ from }}>
          로그인
        </Link>
        <Link className="btn btn-secondary" to="/signup">
          회원가입
        </Link>
      </div>
    </div>
  )
}
