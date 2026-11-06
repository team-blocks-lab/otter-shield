package com.otter.store.order;

import com.otter.store.goods.Goods;
import com.otter.store.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "purchase_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "recipient_name", nullable = false, length = 50)
    private String recipientName;

    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "address_detail", length = 255)
    private String addressDetail;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 주문 저장 시 주문 상품도 함께 저장
    // 주문 삭제 API는 제공하지 않으므로 REMOVE 전파 제외
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> items = new ArrayList<>();

    public PurchaseOrder(
            Member member,
            String recipientName,
            String phone,
            String postalCode,
            String address,
            String addressDetail
    ) {
        this.member = member;
        this.recipientName = recipientName;
        this.phone = phone;
        this.postalCode = postalCode;
        this.address = address;
        this.addressDetail = addressDetail;
    }

    public void addItem(Goods goods, int quantity) {
        // 주문 당시 상품명·가격을 주문 상품에 별도 보관
        OrderItem item = new OrderItem(this, goods, quantity);
        items.add(item);
        totalAmount = totalAmount.add(item.calculateSubtotal());
    }

    @PrePersist
    private void initializeCreatedAt() {
        createdAt = LocalDateTime.now();
    }
}