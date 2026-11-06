import { Link, useParams } from 'react-router-dom'
import OrderDetailView from '../components/OrderDetailView'
import { ErrorBox, Loading } from '../components/States'
import { useOrder } from '../components/useOrder'

export default function OrderDetailPage() {
  const { orderId } = useParams()
  const { order, status, error, reload } = useOrder(orderId)
  return (
    <div className="container page">
      <h1 className="page-title">주문 상세</h1>
      {status === 'loading' && <Loading label="주문 내용을 불러오는 중입니다" />}
      {status === 'error' && <ErrorBox error={error} onRetry={reload} title="주문 내용을 불러오지 못했습니다" />}
      {status === 'ready' && <OrderDetailView order={order} />}
      <p className="page-actions"><Link className="btn btn-secondary" to="/orders">주문 내역으로</Link></p>
    </div>
  )
}
