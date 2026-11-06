package com.otter.store.goods;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    // 판매 중인 상품만 페이지 단위 조회
    Page<Goods> findByActiveTrue(Pageable pageable);

    // 판매 중지 상품과 없는 상품의 상세 조회 제한
    Optional<Goods> findByGoodsIdAndActiveTrue(Long goodsId);
}