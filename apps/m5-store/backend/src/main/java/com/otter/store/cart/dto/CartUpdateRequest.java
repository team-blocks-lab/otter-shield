package com.otter.store.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// 기존 수량을 대체할 최종 수량
public record CartUpdateRequest(

        @NotNull(message = "수량을 입력해 주세요.")
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        @Max(value = 99, message = "수량은 99개 이하여야 합니다.")
        Integer quantity
) {
}