import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'
import { USE_MOCK } from '../config'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'

export default function Layout() {
  const { user, logout } = useAuth()
  const { items } = useCart()
  const navigate = useNavigate()

  async function handleLogout() {
    try {
      await logout()
    } finally {
      navigate('/')
    }
  }

  return (
    <>
      <a className="skip-link" href="#main">
        본문으로 건너뛰기
      </a>
      <header className="site-header">
        <div className="container header-inner">
          <Link to="/" className="brand" aria-label="OTTer Store 홈">
            OTTer Store
          </Link>
          <nav className="nav" aria-label="주요 메뉴">
            <NavLink to="/goods">굿즈</NavLink>
            <NavLink to="/orders">주문 내역</NavLink>
          </nav>
          <div className="header-actions">
            <NavLink to="/cart" className="cart-link">
              장바구니
              {user && items.length > 0 && <span className="cart-count">{items.length}</span>}
            </NavLink>
            {user ? (
              <button type="button" className="link-button" onClick={handleLogout}>
                로그아웃
              </button>
            ) : (
              <NavLink to="/login">로그인</NavLink>
            )}
          </div>
        </div>
      </header>
      {USE_MOCK && (
        <p className="demo-bar" role="note">
          미리보기용 데모 데이터입니다. 실제 상품과 주문이 아닙니다.
        </p>
      )}
      <main id="main">
        <Outlet />
      </main>
      <footer className="site-footer">
        <div className="container footer-inner">
          <span>OTTer Store</span>
          <span>OTTer 공식 굿즈 스토어</span>
        </div>
      </footer>
    </>
  )
}
