import { useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { Empty, ErrorBox, Loading } from './States'
import ProductCard from './ProductCard'

export default function GoodsSection({ goods, status, error, onRetry, heading = '굿즈', headingLevel = 2 }) {
  const { user } = useAuth()
  const { add } = useCart()
  const navigate = useNavigate()
  const location = useLocation()
  const Heading = `h${headingLevel}`

  async function handleAdd(item) {
    if (!user) {
      navigate('/login', { state: { from: location.pathname, notice: '장바구니에 담으려면 로그인이 필요합니다.' } })
      return 'redirect'
    }
    await add(item.goodsId, 1)
    return 'ok'
  }

  return (
    <section className="section" id="goods" aria-labelledby="goods-title">
      <div className="container">
        <Heading id="goods-title" className="section-title">
          {heading}
        </Heading>
        {status === 'loading' && <Loading label="상품을 불러오는 중입니다" />}
        {status === 'error' && <ErrorBox error={error} onRetry={onRetry} title="상품을 불러오지 못했습니다" />}
        {status === 'ready' && goods.length === 0 && <Empty title="아직 등록된 상품이 없습니다" />}
        {status === 'ready' && goods.length > 0 && (
          <ul className="grid">
            {goods.map((g) => (
              <ProductCard key={g.goodsId} goods={g} onAdd={handleAdd} />
            ))}
          </ul>
        )}
      </div>
    </section>
  )
}
