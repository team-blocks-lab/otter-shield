package com.otter.store.goods;

import com.otter.store.goods.dto.GoodsPageResponse;
import com.otter.store.goods.dto.GoodsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    // 비로그인 사용자에게도 공개하는 상품 목록
    @GetMapping
    public GoodsPageResponse getGoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return goodsService.getGoods(page, size);
    }

    @GetMapping("/{goodsId}")
    public GoodsResponse getGoodsDetail(
            @PathVariable Long goodsId
    ) {
        return goodsService.getGoodsDetail(goodsId);
    }
}