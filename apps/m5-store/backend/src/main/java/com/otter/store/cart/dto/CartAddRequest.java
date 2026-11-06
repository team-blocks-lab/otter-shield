package com.otter.store.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// 상품 번호와 추가할 수량만 입력
// 회원 번호 및 상품 가격은 클라이언트 입력 대상에서 제외
public record CartAddRequest(

        @NotNull(message = "상품을 선택해 주세요.")
        @Positive(message = "상품 번호를 확인해 주세요.")
        Long goodsId,

        @NotNull(message = "수량을 입력해 주세요.")
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        @Max(value = 99, message = "수량은 99개 이하여야 합니다.")
        Integer quantity
) {
}