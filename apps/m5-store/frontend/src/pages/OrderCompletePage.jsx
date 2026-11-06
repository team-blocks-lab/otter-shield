import { Link, useParams } from 'react-router-dom'
import OrderDetailView from '../components/OrderDetailView'
import { ErrorBox, Loading } from '../components/States'
import { useOrder } from '../components/useOrder'

export default function OrderCompletePage() {
  const { orderId } = useParams()
  const { order, status, error, reload } = useOrder(orderId)
  return (
    <div className="container page">
      <h1 className="page-title">주문이 접수되었습니다</h1>
      {status === 'loading' && <Loading label="주문 내용을 불러오는 중입니다" />}
      {status === 'error' && <ErrorBox error={error} onRetry={reload} title="주문 내용을 불러오지 못했습니다" />}
      {status === 'ready' && <OrderDetailView order={order} />}
      <div className="row page-actions">
        <Link className="btn btn-secondary" to="/orders">주문 내역</Link>
        <Link className="btn btn-primary" to="/goods">쇼핑 계속하기</Link>
      </div>
    </div>
  )
}
