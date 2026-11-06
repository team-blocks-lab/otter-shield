import { useCallback, useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { api } from '../api'
import { Empty, ErrorBox, Loading } from '../components/States'
import { formatDate, formatWon } from '../format'

export default function OrdersPage() {
  const [params, setParams] = useSearchParams()
  const page = Math.max(0, Number(params.get('page')) || 0)
  const [state, setState] = useState({ data: null, status: 'loading', error: null })

  const load = useCallback(() => {
    let alive = true
    setState({ data: null, status: 'loading', error: null })
    api
      .listOrders(page)
      .then((data) => alive && setState({ data, status: 'ready', error: null }))
      .catch((error) => alive && setState({ data: null, status: 'error', error }))
    return () => {
      alive = false
    }
  }, [page])
  useEffect(() => load(), [load])

  const { data, status, error } = state
  return (
    <div className="container page">
      <h1 className="page-title">주문 내역</h1>
      {status === 'loading' && <Loading label="주문 내역을 불러오는 중입니다" />}
      {status === 'error' && <ErrorBox error={error} onRetry={load} title="주문 내역을 불러오지 못했습니다" />}
      {status === 'ready' && data.orders.length === 0 && (
        <Empty title="주문 내역이 없습니다" action={<Link className="btn btn-primary" to="/goods">굿즈 둘러보기</Link>}>
          첫 주문을 시작해 보세요.
        </Empty>
      )}
      {status === 'ready' && data.orders.length > 0 && (
        <>
          <ul className="order-list">
            {data.orders.map((o) => (
              <li key={o.orderId}>
                <Link className="order-link" to={`/orders/${o.orderId}`}>
                  <span className="num order-no">주문번호 {o.orderId}</span>
                  <span className="muted">{formatDate(o.createdAt)}</span>
                  <span className="num order-amount">{formatWon(o.totalAmount)}</span>
                </Link>
              </li>
            ))}
          </ul>
          {data.totalPages > 1 && (
            <div className="row page-actions">
              <button type="button" className="btn btn-secondary" disabled={page <= 0} onClick={() => setParams({ page: page - 1 })}>이전</button>
              <span className="num muted">{page + 1} / {data.totalPages}</span>
              <button type="button" className="btn btn-secondary" disabled={page + 1 >= data.totalPages} onClick={() => setParams({ page: page + 1 })}>다음</button>
            </div>
          )}
        </>
      )}
    </div>
  )
}
