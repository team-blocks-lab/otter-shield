package com.otter.store.order;

import com.otter.store.auth.AuthController;
import com.otter.store.order.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> createOrder(
            @SessionAttribute(
                    name = AuthController.LOGIN_MEMBER_ID,
                    required = false
            ) Long memberId,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        Long orderId = orderService.createOrder(memberId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("orderId", orderId));
    }

    @GetMapping
    public OrderPageResponse getOrders(
            @SessionAttribute(
                    name = AuthController.LOGIN_MEMBER_ID,
                    required = false
            ) Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getOrders(memberId, page, size);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @SessionAttribute(
                    name = AuthController.LOGIN_MEMBER_ID,
                    required = false
            ) Long memberId,
            @PathVariable Long orderId
    ) {
        return orderService.getOrder(memberId, orderId);
    }
}