package com.otter.store.order;

import com.otter.store.goods.Goods;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private PurchaseOrder order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "goods_id", nullable = false)
    private Goods goods;

    // 상품 정보가 변경되어도 유지할 주문 당시 상품명
    @Column(name = "goods_name", nullable = false, length = 100)
    private String goodsName;

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public OrderItem(PurchaseOrder order, Goods goods, int quantity) {
        if (quantity < 1 || quantity > 99) {
            throw new IllegalArgumentException("잘못된 주문 수량입니다.");
        }

        this.order = order;
        this.goods = goods;
        this.goodsName = goods.getName();
        this.unitPrice = goods.getPrice();
        this.quantity = quantity;
    }

    public BigDecimal calculateSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}