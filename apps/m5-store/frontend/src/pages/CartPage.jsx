import { useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import GoodsImage from '../components/GoodsImage'
import { Empty, ErrorBox, Loading } from '../components/States'
import { useCart } from '../context/CartContext'
import { formatWon } from '../format'

const MAX_QTY = 99 // 화면 입력 상한(서버 규칙 아님)

function CartRow({ item, checked, onToggle }) {
  const { setQuantity, remove } = useCart()
  const [pending, setPending] = useState(false)
  const [error, setError] = useState('')
  const orderable = item.active !== false

  async function run(action) {
    if (pending) return
    setPending(true)
    setError('')
    try {
      await action()
    } catch (e) {
      setError(e.message)
      setPending(false)
    }
  }

  return (
    <li className={`cart-row${orderable ? '' : ' is-inactive'}`}>
      <input
        type="checkbox"
        className="check"
        checked={checked}
        disabled={!orderable}
        onChange={() => onToggle(item.cartItemId)}
        aria-label={`${item.goodsName} 선택`}
      />
      <div className="cart-thumb">
        <GoodsImage imageKey={item.imageKey} alt={item.goodsName} />
      </div>
      <div className="cart-info">
        <p className="cart-name">{item.goodsName}</p>
        <p className="muted num">{formatWon(item.unitPrice)}</p>
        {!orderable && <p className="cart-warn">지금은 주문할 수 없는 상품입니다.</p>}
        <p className="field-error" role="alert">
          {error}
        </p>
      </div>
      <div className="stepper" role="group" aria-label={`${item.goodsName} 수량`}>
        <button type="button" disabled={pending || item.quantity <= 1} onClick={() => run(() => setQuantity(item.cartItemId, item.quantity - 1))} aria-label="수량 줄이기">
          −
        </button>
        <span className="num" aria-live="polite">
          {item.quantity}
        </span>
        <button type="button" disabled={pending || item.quantity >= MAX_QTY} onClick={() => run(() => setQuantity(item.cartItemId, item.quantity + 1))} aria-label="수량 늘리기">
          +
        </button>
      </div>
      <p className="cart-sub num">{formatWon(item.subtotal ?? item.unitPrice * item.quantity)}</p>
      <button type="button" className="link-button danger" disabled={pending} onClick={() => run(() => remove(item.cartItemId))}>
        삭제
      </button>
    </li>
  )
}

export default function CartPage() {
  const { items, status, error, refresh } = useCart()
  const navigate = useNavigate()
  // 선택 해제한 항목만 기억합니다. 기본은 주문 가능한 항목 전체 선택입니다.
  const [deselected, setDeselected] = useState(() => new Set())

  const orderable = useMemo(() => items.filter((i) => i.active !== false), [items])
  const selected = useMemo(() => orderable.filter((i) => !deselected.has(i.cartItemId)), [orderable, deselected])
  const selectedTotal = selected.reduce((sum, i) => sum + (i.subtotal ?? i.unitPrice * i.quantity), 0)
  const allSelected = orderable.length > 0 && selected.length === orderable.length

  function toggle(id) {
    setDeselected((prev) => {
      const next = new Set(prev)
      if (next.has(id)) next.delete(id)
      else next.add(id)
      return next
    })
  }
  function toggleAll() {
    setDeselected(allSelected ? new Set(orderable.map((i) => i.cartItemId)) : new Set())
  }

  return (
    <div className="container page">
      <h1 className="page-title">장바구니</h1>
      {status === 'loading' && items.length === 0 && <Loading label="장바구니를 불러오는 중입니다" />}
      {status === 'error' && <ErrorBox error={error} onRetry={refresh} title="장바구니를 불러오지 못했습니다" />}
      {status === 'ready' && items.length === 0 && (
        <Empty title="장바구니가 비어 있습니다" action={<Link className="btn btn-primary" to="/goods">굿즈 둘러보기</Link>}>
          마음에 드는 굿즈를 담아 보세요.
        </Empty>
      )}
      {items.length > 0 && (
        <div className="split">
          <div>
            <label className="select-all">
              <input type="checkbox" className="check" checked={allSelected} disabled={orderable.length === 0} onChange={toggleAll} />
              <span>전체 선택 ({selected.length}/{orderable.length})</span>
            </label>
            <ul className="cart-list">
              {items.map((item) => (
                <CartRow key={item.cartItemId} item={item} checked={selected.some((s) => s.cartItemId === item.cartItemId)} onToggle={toggle} />
              ))}
            </ul>
          </div>
          <aside className="summary" aria-label="주문 요약">
            <h2 className="summary-title">선택한 상품</h2>
            <dl className="summary-list">
              <div>
                <dt>선택 상품</dt>
                <dd className="num">{selected.length}개</dd>
              </div>
              <div className="summary-total">
                <dt>선택 금액</dt>
                <dd className="num">{formatWon(selectedTotal)}</dd>
              </div>
            </dl>
            <button
              type="button"
              className="btn btn-primary btn-block"
              disabled={selected.length === 0}
              onClick={() => navigate('/checkout', { state: { cartItemIds: selected.map((i) => i.cartItemId) } })}
            >
              선택 상품 주문하기
            </button>
            {selected.length === 0 && <p className="muted small">주문할 상품을 선택해 주세요.</p>}
          </aside>
        </div>
      )}
    </div>
  )
}
