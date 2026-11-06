package com.otter.store.cart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 장바구니 표시 시 필요한 상품을 함께 조회하여 반복 쿼리 축소
    @EntityGraph(attributePaths = "goods")
    List<CartItem> findByMember_MemberIdOrderByCartItemIdDesc(
            Long memberId
    );

    Optional<CartItem> findByMember_MemberIdAndGoods_GoodsId(
            Long memberId,
            Long goodsId
    );

    // 항목 번호와 로그인 회원 번호를 함께 사용한 소유권 제한
    Optional<CartItem> findByCartItemIdAndMember_MemberId(
            Long cartItemId,
            Long memberId
    );
}