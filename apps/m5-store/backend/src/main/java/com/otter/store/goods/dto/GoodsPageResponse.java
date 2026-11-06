package com.otter.store.goods.dto;

import java.util.List;

// 상품 목록과 추가 조회에 필요한 페이지 정보
public record GoodsPageResponse(
        List<GoodsResponse> goods,
        int page,
        int totalPages,
        long totalElements
) {
}