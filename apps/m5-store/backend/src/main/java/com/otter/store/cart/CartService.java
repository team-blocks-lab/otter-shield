package com.otter.store.cart;

import com.otter.store.auth.AuthService;
import com.otter.store.cart.dto.*;
import com.otter.store.goods.Goods;
import com.otter.store.goods.GoodsRepository;
import com.otter.store.member.Member;
import com.otter.store.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final GoodsRepository goodsRepository;
    private final CartItemRepository cartItemRepository;

    public CartResponse getCart(Long memberId) {
        authService.requireMember(memberId);

        List<CartItemResponse> items = cartItemRepository
                .findByMember_MemberIdOrderByCartItemIdDesc(memberId)
                .stream()
                .map(this::toResponse)
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(items, totalAmount);
    }

    @Transactional
    public CartItemResponse addItem(
            Long memberId,
            CartAddRequest request
    ) {
        Member member = lockMember(memberId);
        requireValidQuantity(request.quantity());

        Goods goods = goodsRepository
                .findByGoodsIdAndActiveTrue(request.goodsId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "판매 중인 상품을 찾을 수 없습니다."
                ));

        CartItem item = cartItemRepository
                .findByMember_MemberIdAndGoods_GoodsId(
                        memberId,
                        goods.getGoodsId()
                )
                .orElse(null);

        if (item == null) {
            item = cartItemRepository.save(
                    new CartItem(member, goods, request.quantity())
            );
        } else {
            // 같은 상품을 다시 담는 요청의 수량 합산
            int nextQuantity = item.getQuantity() + request.quantity();
            requireValidQuantity(nextQuantity);
            item.changeQuantity(nextQuantity);
        }

        return toResponse(item);
    }

    @Transactional
    public void updateQuantity(
            Long memberId,
            Long cartItemId,
            CartUpdateRequest request
    ) {
        lockMember(memberId);
        requireValidQuantity(request.quantity());

        CartItem item = findOwnedItem(memberId, cartItemId);

        if (!item.getGoods().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "판매가 중지된 상품입니다. 장바구니에서 삭제해 주세요."
            );
        }

        // 관리 중인 Entity의 변경 감지를 통한 수량 저장
        item.changeQuantity(request.quantity());
    }

    @Transactional
    public void deleteItem(Long memberId, Long cartItemId) {
        lockMember(memberId);

        CartItem item = findOwnedItem(memberId, cartItemId);

        // 판매 여부와 무관하게 본인 장바구니 항목 삭제 허용
        cartItemRepository.delete(item);
    }

    private Member lockMember(Long memberId) {
        // 미로그인 요청의 DB 변경 차단
        authService.requireMember(memberId);

        // 추가·수정·삭제에서 동일한 회원 잠금 규칙 적용
        return memberRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "로그인이 필요합니다."
                ));
    }

    private CartItem findOwnedItem(Long memberId, Long cartItemId) {
        // 타인 항목의 존재 여부를 노출하지 않는 조회 조건
        return cartItemRepository
                .findByCartItemIdAndMember_MemberId(
                        cartItemId,
                        memberId
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "장바구니 항목을 찾을 수 없습니다."
                ));
    }

    private void requireValidQuantity(Integer quantity) {
        if (quantity == null || quantity < 1 || quantity > 99) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "상품당 수량은 1~99개로 입력해 주세요."
            );
        }
    }

    private CartItemResponse toResponse(CartItem item) {
        Goods goods = item.getGoods();

        // 가격 및 소계의 서버 계산
        BigDecimal subtotal = goods.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                item.getCartItemId(),
                goods.getGoodsId(),
                goods.getName(),
                goods.getImageKey(),
                goods.getPrice(),
                item.getQuantity(),
                subtotal,
                goods.isActive()
        );
    }
}