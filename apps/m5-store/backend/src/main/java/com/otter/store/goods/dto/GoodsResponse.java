package com.otter.store.goods.dto;

import java.math.BigDecimal;

// 상품 화면에 제공하는 조회 정보
public record GoodsResponse(
        Long goodsId,
        String name,
        String description,
        BigDecimal price,
        String imageKey
) {
}