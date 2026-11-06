package com.otter.store.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, Long> {

    // 로그인 회원의 주문만 최신순 조회
    Page<PurchaseOrder> findByMember_MemberId(
            Long memberId,
            Pageable pageable
    );

    // 소유자 조건을 포함한 주문 상세 및 주문 상품 조회
    @EntityGraph(attributePaths = "items")
    Optional<PurchaseOrder> findByOrderIdAndMember_MemberId(
            Long orderId,
            Long memberId
    );
}