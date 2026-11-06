package com.otter.store.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 본인 주문 상세 응답
public record OrderResponse(
        Long orderId,
        BigDecimal totalAmount,
        String recipientName,
        String phone,
        String postalCode,
        String address,
        String addressDetail,
        LocalDateTime createdAt,
        List<Item> items
) {

    // 주문 당시 저장된 값으로 구성하는 상품 내역
    public record Item(
            Long orderItemId,
            String goodsName,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal subtotal
    ) {
    }
}