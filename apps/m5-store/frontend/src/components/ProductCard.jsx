import { useRef, useState } from 'react'
import { USE_MOCK } from '../config'
import { formatWon } from '../format'
import GoodsImage from './GoodsImage'

export default function ProductCard({ goods, onAdd }) {
  const [pending, setPending] = useState(false)
  const [message, setMessage] = useState('')
  const [failed, setFailed] = useState(false)
  const busy = useRef(false)

  async function handleAdd() {
    if (busy.current) return
    busy.current = true
    setPending(true)
    setMessage('')
    setFailed(false)
    try {
      const result = await onAdd(goods)
      if (result !== 'redirect') setMessage('장바구니에 담았습니다.')
    } catch (e) {
      setFailed(true)
      setMessage(e.message || '담지 못했습니다.')
    } finally {
      busy.current = false
      setPending(false)
    }
  }

  return (
    <li className="card">
      <div className="card-media">
        <GoodsImage imageKey={goods.imageKey} alt={goods.name} />
        {USE_MOCK && <span className="demo-tag">데모</span>}
      </div>
      <div className="card-body">
        <h3 className="card-title">{goods.name}</h3>
        <p className="card-desc">{goods.description}</p>
        <p className="card-price num">{formatWon(goods.price)}</p>
        <button type="button" className="btn btn-primary btn-block" onClick={handleAdd} disabled={pending}>
          {pending ? '담는 중' : '장바구니 담기'}
        </button>
        <p className={`card-msg${failed ? ' is-error' : ''}`} aria-live="polite">
          {message}
        </p>
      </div>
    </li>
  )
}
