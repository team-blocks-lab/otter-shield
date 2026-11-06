import GoodsImage from '../components/GoodsImage'
import GoodsSection from '../components/GoodsSection'
import { useGoods } from '../components/useGoods'

export default function HomePage() {
  const { goods, status, error, reload } = useGoods()
  const [first, second] = goods

  return (
    <>
      <section className="hero" aria-labelledby="hero-title">
        <div className="container hero-inner">
          <div className="hero-copy">
            <h1 id="hero-title" className="hero-title">
              OTTer의 하루를 담은 굿즈
            </h1>
            <p className="hero-lead">공식 굿즈를 한곳에서 골라 보세요.</p>
            <a className="btn btn-primary" href="#goods">
              굿즈 보러 가기
            </a>
          </div>
          <div className="hero-photos" aria-hidden="true">
            <div className="hero-photo hero-photo-main">
              <GoodsImage imageKey={first?.imageKey} eager />
            </div>
            <div className="hero-photo hero-photo-sub">
              <GoodsImage imageKey={second?.imageKey} eager />
            </div>
          </div>
        </div>
      </section>
      <GoodsSection goods={goods} status={status} error={error} onRetry={reload} heading="전체 굿즈" />
    </>
  )
}
