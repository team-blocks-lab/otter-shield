import { useState } from 'react'
import { imageUrl, PLACEHOLDER } from '../format'

// 상품 이미지가 없거나 불러오지 못하면 로컬 SVG 기본 이미지를 보여줍니다.
export default function GoodsImage({ imageKey, alt = '', eager = false }) {
  const [failed, setFailed] = useState(false)
  const src = !failed && imageUrl(imageKey) ? imageUrl(imageKey) : PLACEHOLDER
  return (
    <img
      src={src}
      alt={alt}
      loading={eager ? 'eager' : 'lazy'}
      decoding="async"
      onError={() => setFailed(true)}
    />
  )
}
