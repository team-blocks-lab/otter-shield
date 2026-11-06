package com.otter.store.cart.dto;

import java.math.BigDecimal;

// 현재 상품 가격을 기준으로 계산한 장바구니 항목
public record CartItemResponse(
        Long cartItemId,
        Long goodsId,
        String goodsName,
        String imageKey,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal subtotal,
        boolean active
) {
}