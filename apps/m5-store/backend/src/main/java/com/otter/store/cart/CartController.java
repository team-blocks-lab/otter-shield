package com.otter.store.cart;

import com.otter.store.auth.AuthController;
import com.otter.store.cart.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse getCart(
            @SessionAttribute(
                    name = AuthController.LOGIN_MEMBER_ID,
                    required = false
            ) Long memberId
    ) {
        return cartService.getCart(memberId);
    }

    @PostMapping("/items")
    public CartItemResponse addItem(
            @SessionAttribute(
                    name = AuthController.LOGIN_MEMBER_ID,
                    required = false
            ) Long memberId,
            @Valid @RequestBody CartAddRequest request
    ) {
        // 새 항목 생성과 기존 수량 증가를 모두 처리하므로 200 응답
        return cartService.addItem(memberId, request);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateQuantity(
            @SessionAttribute(
                    name = AuthController.LOGIN_MEMBER_ID,
                    required = false
            ) Long memberId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartUpdateRequest request
    ) {
        cartService.updateQuantity(memberId, cartItemId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteItem(
            @SessionAttribute(
                    name = AuthController.LOGIN_MEMBER_ID,
                    required = false
            ) Long memberId,
            @PathVariable Long cartItemId
    ) {
        cartService.deleteItem(memberId, cartItemId);

        return ResponseEntity.noContent().build();
    }
}