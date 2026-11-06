import { formatDate, formatWon } from '../format'

export default function OrderDetailView({ order }) {
  return (
    <div className="split">
      <div>
        <h2 className="block-title">주문 상품</h2>
        <ul className="order-items">
          {order.items?.map((i) => (
            <li key={i.orderItemId} className="order-item order-item-text">
              <p className="cart-name">{i.goodsName}</p>
              <p className="muted num">{formatWon(i.unitPrice)} x {i.quantity}개</p>
              <p className="num">{formatWon(i.subtotal)}</p>
            </li>
          ))}
        </ul>
      </div>
      <aside className="summary">
        <h2 className="summary-title">주문 정보</h2>
        <dl className="summary-list">
          <div><dt>주문번호</dt><dd className="num">{order.orderId}</dd></div>
          <div><dt>주문일시</dt><dd>{formatDate(order.createdAt)}</dd></div>
          <div className="summary-total"><dt>합계</dt><dd className="num">{formatWon(order.totalAmount)}</dd></div>
        </dl>
        <h2 className="summary-title">배송지</h2>
        <p>{order.recipientName}</p>
        <p className="muted">{order.phone}</p>
        <p className="muted">({order.postalCode}) {order.address} {order.addressDetail}</p>
      </aside>
    </div>
  )
}
