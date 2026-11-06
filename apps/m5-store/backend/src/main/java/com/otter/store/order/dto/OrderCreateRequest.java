package com.otter.store.order.dto;

import jakarta.validation.constraints.*;

import java.util.List;

// 선택한 장바구니 항목과 배송 정보만 입력
// 회원 번호·가격·주문 총액은 서버에서 결정
public record OrderCreateRequest(

        @NotEmpty(message = "주문할 상품을 선택해 주세요.")
        @Size(max = 100, message = "한 번에 최대 100개 항목까지 주문할 수 있습니다.")
        List<
                @NotNull
                @Positive
                        Long
                > cartItemIds,

        @NotBlank(message = "받는 사람을 입력해 주세요.")
        @Size(max = 50)
        String recipientName,

        @NotBlank(message = "연락처를 입력해 주세요.")
        @Size(max = 30)
        String phone,

        @NotBlank(message = "우편번호를 입력해 주세요.")
        @Size(max = 20)
        String postalCode,

        @NotBlank(message = "주소를 입력해 주세요.")
        @Size(max = 255)
        String address,

        @Size(max = 255)
        String addressDetail
) {
}