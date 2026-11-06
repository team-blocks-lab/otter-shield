package com.otter.store.order.dto;

import java.util.List;

public record OrderPageResponse(
        List<OrderSummaryResponse> orders,
        int page,
        int totalPages,
        long totalElements
) {
}