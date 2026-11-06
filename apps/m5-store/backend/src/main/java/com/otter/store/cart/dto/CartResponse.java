package com.otter.store.cart.dto;

import java.math.BigDecimal;
import java.util.List;

// 장바구니 전체 항목 및 표시용 합계
// 실제 주문 금액은 주문 시 서버에서 다시 계산
public record CartResponse(
        List<CartItemResponse> items,
        BigDecimal totalAmount
) {
}