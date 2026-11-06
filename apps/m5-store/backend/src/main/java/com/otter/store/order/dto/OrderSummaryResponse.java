package com.otter.store.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 주문 목록 카드에 필요한 정보
public record OrderSummaryResponse(
        Long orderId,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}