import { useRef, useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { api } from '../api'
import GoodsImage from '../components/GoodsImage'
import { ErrorBox, Loading } from '../components/States'
import { useCart } from '../context/CartContext'
import { formatWon } from '../format'

const FIELDS = [
  { name: 'recipientName', label: '수령인', autoComplete: 'name' },
  { name: 'phone', label: '연락처', autoComplete: 'tel', inputMode: 'tel' },
  { name: 'postalCode', label: '우편번호', autoComplete: 'postal-code', inputMode: 'numeric' },
  { name: 'address', label: '주소', autoComplete: 'address-line1' },
  { name: 'addressDetail', label: '상세주소', autoComplete: 'address-line2' },
]

export default function CheckoutPage() {
  const location = useLocation()
  const navigate = useNavigate()
  const { items, status, error, refresh } = useCart()
  const ids = location.state?.cartItemIds
  const [form, setForm] = useState({ recipientName: '', phone: '', postalCode: '', address: '', addressDetail: '' })
  const [fieldErrors, setFieldErrors] = useState({})
  const [formError, setFormError] = useState('')
  const [pending, setPending] = useState(false)
  const busy = useRef(false) // 제출 중 중복 클릭 차단

  if (!Array.isArray(ids) || ids.length === 0) return <Navigate to="/cart" replace />
  if (status === 'loading' && items.length === 0) return <div className="container page"><Loading /></div>
  if (status === 'error') return <div className="container page"><ErrorBox error={error} onRetry={refresh} /></div>

  const chosen = items.filter((i) => ids.includes(i.cartItemId))
  const total = chosen.reduce((sum, i) => sum + (i.subtotal ?? i.unitPrice * i.quantity), 0)

  function change(event) {
    setForm((f) => ({ ...f, [event.target.name]: event.target.value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    if (busy.current) return
    const missing = {}
    FIELDS.forEach(({ name, label }) => {
      if (!form[name].trim()) missing[name] = `${label}을(를) 입력해 주세요.`
    })
    if (Object.keys(missing).length) {
      setFieldErrors(missing)
      setFormError('')
      return
    }
    busy.current = true
    setPending(true)
    setFieldErrors({})
    setFormError('')
    try {
      const { orderId } = await api.createOrder({
        cartItemIds: chosen.map((i) => i.cartItemId),
        recipientName: form.recipientName.trim(),
        phone: form.phone.trim(),
        postalCode: form.postalCode.trim(),
        address: form.address.trim(),
        addressDetail: form.addressDetail.trim(),
      })
      refresh()
      navigate(`/orders/${orderId}/complete`, { replace: true })
    } catch (e) {
      setFormError(e.message)
      setFieldErrors(e.fieldErrors || {})
      busy.current = false
      setPending(false)
    }
  }

  return (
    <div className="container page">
      <h1 className="page-title">주문서</h1>
      {chosen.length === 0 ? (
        <div className="state">
          <p>선택한 상품이 장바구니에 없습니다.</p>
          <Link className="btn btn-secondary" to="/cart">장바구니로 돌아가기</Link>
        </div>
      ) : (
        <form className="split" onSubmit={handleSubmit} noValidate>
          <div>
            <h2 className="block-title">배송지</h2>
            <div className="form">
              {FIELDS.map(({ name, label, ...rest }) => (
                <label className="field" key={name}>
                  <span>{label}</span>
                  <input name={name} value={form[name]} onChange={change} aria-invalid={Boolean(fieldErrors[name])} {...rest} />
                  <small className="field-error">{fieldErrors[name]}</small>
                </label>
              ))}
            </div>
            <h2 className="block-title">주문 상품</h2>
            <ul className="order-items">
              {chosen.map((i) => (
                <li key={i.cartItemId} className="order-item">
                  <div className="cart-thumb"><GoodsImage imageKey={i.imageKey} alt={i.goodsName} /></div>
                  <p className="cart-name">{i.goodsName}</p>
                  <p className="muted num">{i.quantity}개</p>
                  <p className="num">{formatWon(i.subtotal ?? i.unitPrice * i.quantity)}</p>
                </li>
              ))}
            </ul>
          </div>
          <aside className="summary" aria-label="결제 금액">
            <h2 className="summary-title">주문 합계</h2>
            <dl className="summary-list">
              <div><dt>상품 {chosen.length}종</dt><dd className="num">{formatWon(total)}</dd></div>
              <div className="summary-total"><dt>합계</dt><dd className="num">{formatWon(total)}</dd></div>
            </dl>
            <p className="form-error" role="alert">{formError}</p>
            <button className="btn btn-primary btn-block" disabled={pending}>
              {pending ? '주문하는 중' : '주문 제출'}
            </button>
            <p className="muted small">실제 결제는 이루어지지 않습니다.</p>
          </aside>
        </form>
      )}
    </div>
  )
}
