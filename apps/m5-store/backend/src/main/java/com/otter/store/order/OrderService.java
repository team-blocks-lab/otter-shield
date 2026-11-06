package com.otter.store.order;

import com.otter.store.auth.AuthService;
import com.otter.store.cart.CartItem;
import com.otter.store.cart.CartItemRepository;
import com.otter.store.member.Member;
import com.otter.store.member.MemberRepository;
import com.otter.store.order.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;
    private final PurchaseOrderRepository orderRepository;

    @Transactional
    public Long createOrder(Long memberId, OrderCreateRequest request) {
        authService.requireMember(memberId);

        // 장바구니 변경과 주문에서 동일한 회원 잠금 사용
        // 같은 항목의 동시 주문 및 수량 변경과의 충돌 방지
        Member member = memberRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "로그인이 필요합니다."
                ));

        List<Long> requestedIds = request.cartItemIds();

        if (requestedIds == null || requestedIds.isEmpty()
                || requestedIds.size() > 100
                || requestedIds.stream().anyMatch(
                id -> id == null || id <= 0
        )) {
            throw badRequest("주문할 장바구니 항목을 확인해 주세요.");
        }

        Set<Long> selectedIds = new HashSet<>(requestedIds);

        if (selectedIds.size() != requestedIds.size()) {
            throw badRequest("같은 장바구니 항목을 중복 선택할 수 없습니다.");
        }

        // 로그인 회원의 장바구니에서 선택 항목만 추출
        List<CartItem> selectedItems = cartItemRepository
                .findByMember_MemberIdOrderByCartItemIdDesc(memberId)
                .stream()
                .filter(item -> selectedIds.contains(item.getCartItemId()))
                .toList();

        // 타인 항목·삭제된 항목·존재하지 않는 항목을 모두 거절
        if (selectedItems.size() != selectedIds.size()) {
            throw badRequest("주문할 수 없는 장바구니 항목이 포함되어 있습니다.");
        }

        PurchaseOrder order = new PurchaseOrder(
                member,
                request.recipientName(),
                request.phone(),
                request.postalCode(),
                request.address(),
                request.addressDetail()
        );

        for (CartItem item : selectedItems) {
            if (!item.getGoods().isActive()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "판매가 중지된 상품이 포함되어 있습니다."
                );
            }

            if (item.getQuantity() < 1 || item.getQuantity() > 99) {
                throw badRequest("주문 수량을 확인해 주세요.");
            }

            // DB의 상품 가격으로 주문 당시 금액 확정
            order.addItem(item.getGoods(), item.getQuantity());
        }

        // DECIMAL(14, 2)의 저장 범위 초과 차단
        if (order.getTotalAmount().compareTo(
                new BigDecimal("999999999999.99")
        ) > 0) {
            throw badRequest("주문 가능한 총액을 초과했습니다.");
        }

        PurchaseOrder saved = orderRepository.save(order);

        // 주문 저장과 선택 항목 삭제를 같은 트랜잭션으로 처리
        // 선택하지 않은 장바구니 항목은 유지
        cartItemRepository.deleteAll(selectedItems);

        return saved.getOrderId();
    }

    public OrderPageResponse getOrders(
            Long memberId,
            int page,
            int size
    ) {
        authService.requireMember(memberId);

        if (page < 0 || size < 1 || size > 50) {
            throw badRequest("페이지는 0 이상, 조회 개수는 1~50으로 입력해 주세요.");
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("orderId")
                )
        );

        Page<PurchaseOrder> result =
                orderRepository.findByMember_MemberId(memberId, pageable);

        return new OrderPageResponse(
                result.getContent().stream()
                        .map(order -> new OrderSummaryResponse(
                                order.getOrderId(),
                                order.getTotalAmount(),
                                order.getCreatedAt()
                        ))
                        .toList(),
                result.getNumber(),
                result.getTotalPages(),
                result.getTotalElements()
        );
    }

    public OrderResponse getOrder(Long memberId, Long orderId) {
        authService.requireMember(memberId);

        PurchaseOrder order = orderRepository
                .findByOrderIdAndMember_MemberId(orderId, memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "주문을 찾을 수 없습니다."
                ));

        return new OrderResponse(
                order.getOrderId(),
                order.getTotalAmount(),
                order.getRecipientName(),
                order.getPhone(),
                order.getPostalCode(),
                order.getAddress(),
                order.getAddressDetail(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(item -> new OrderResponse.Item(
                                item.getOrderItemId(),
                                item.getGoodsName(),
                                item.getUnitPrice(),
                                item.getQuantity(),
                                item.calculateSubtotal()
                        ))
                        .toList()
        );
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}