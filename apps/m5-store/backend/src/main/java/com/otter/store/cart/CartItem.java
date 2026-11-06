package com.otter.store.cart;

import com.otter.store.goods.Goods;
import com.otter.store.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "cart_item",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cart_member_goods",
                columnNames = {"member_id", "goods_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "goods_id", nullable = false)
    private Goods goods;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public CartItem(Member member, Goods goods, int quantity) {
        this.member = member;
        this.goods = goods;
        changeQuantity(quantity);
    }

    public void changeQuantity(int quantity) {
        // HTTP 요청 외의 호출에서도 동일한 수량 규칙 유지
        if (quantity < 1 || quantity > 99) {
            throw new IllegalArgumentException(
                    "수량은 1~99개로 입력해 주세요."
            );
        }

        this.quantity = quantity;
    }
}