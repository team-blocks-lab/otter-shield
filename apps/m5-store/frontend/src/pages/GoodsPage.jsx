import GoodsSection from '../components/GoodsSection'
import { useGoods } from '../components/useGoods'

export default function GoodsPage() {
  const { goods, status, error, reload } = useGoods()
  return <GoodsSection goods={goods} status={status} error={error} onRetry={reload} heading="굿즈" headingLevel={1} />
}
