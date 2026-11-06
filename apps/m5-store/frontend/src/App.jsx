import { Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
import RequireAuth from './components/RequireAuth'
import HomePage from './pages/HomePage'
import GoodsPage from './pages/GoodsPage'
import LoginPage from './pages/LoginPage'
import SignupPage from './pages/SignupPage'
import CartPage from './pages/CartPage'
import CheckoutPage from './pages/CheckoutPage'
import OrdersPage from './pages/OrdersPage'
import OrderDetailPage from './pages/OrderDetailPage'
import OrderCompletePage from './pages/OrderCompletePage'

const guard = (element) => <RequireAuth>{element}</RequireAuth>

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="goods" element={<GoodsPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="signup" element={<SignupPage />} />
        <Route path="cart" element={guard(<CartPage />)} />
        <Route path="checkout" element={guard(<CheckoutPage />)} />
        <Route path="orders" element={guard(<OrdersPage />)} />
        <Route path="orders/:orderId" element={guard(<OrderDetailPage />)} />
        <Route path="orders/:orderId/complete" element={guard(<OrderCompletePage />)} />
        <Route
          path="*"
          element={
            <div className="container page">
              <h1 className="page-title">페이지를 찾을 수 없습니다</h1>
            </div>
          }
        />
      </Route>
    </Routes>
  )
}
