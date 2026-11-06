package com.otter.store.goods;

import com.otter.store.goods.dto.GoodsPageResponse;
import com.otter.store.goods.dto.GoodsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoodsService {

    private final GoodsRepository goodsRepository;

    public GoodsPageResponse getGoods(int page, int size) {
        // 잘못된 페이지 및 과도한 일괄 조회 차단
        if (page < 0 || size < 1 || size > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "페이지는 0 이상, 조회 개수는 1~50으로 입력해 주세요."
            );
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("goodsId")
                )
        );

        Page<Goods> result = goodsRepository.findByActiveTrue(pageable);

        return new GoodsPageResponse(
                result.getContent().stream()
                        .map(this::toResponse)
                        .toList(),
                result.getNumber(),
                result.getTotalPages(),
                result.getTotalElements()
        );
    }

    public GoodsResponse getGoodsDetail(Long goodsId) {
        Goods goods = goodsRepository
                .findByGoodsIdAndActiveTrue(goodsId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "상품을 찾을 수 없습니다."
                ));

        return toResponse(goods);
    }

    private GoodsResponse toResponse(Goods goods) {
        return new GoodsResponse(
                goods.getGoodsId(),
                goods.getName(),
                goods.getDescription(),
                goods.getPrice(),
                goods.getImageKey()
        );
    }
}