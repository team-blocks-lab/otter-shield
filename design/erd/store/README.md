# Store ERD

OTT 회원이 굿즈를 조회하고 장바구니에 담아 모의 주문하는 Store 서비스입니다.

## 주요 기능

- 상품 목록·상세 조회
- 장바구니 상품 추가·수량 변경·삭제
- 장바구니 전체 또는 선택 상품의 모의 주문
- 주문 시 배송 정보 입력과 내 주문 목록·상세 조회
- OTT와 공통 Redis 세션을 이용한 로그인 유지

## ERD

상품·장바구니 항목·주문·주문 상품의 데이터 구조입니다. 주문 한 건에 여러 주문 상품을 저장합니다. 회원 정보는 OTT에서 관리하고, Store에는 별도 회원 테이블을 두지 않습니다.

![Store ERD](store_ERD.drawio.png)

[ERD 파일](store_ERD.drawio.png)

## 주요 컬럼을 둔 이유

| 컬럼 | 사용 목적 |
|---|---|
| `member_id` | 세션에서 확인한 OTT 회원과 장바구니·주문 연결. 다른 서비스 DB의 회원 테이블에 직접 FK를 걸지 않음 |
| `image_key` | 상품 사진의 저장 식별값 보관. 화면에는 조회용 URL로 변환해 전달 |
| `active` | 판매 가능 여부 구분. 판매 중지 상품의 신규 담기·주문 제한과 기존 주문 참조 유지 |
| `goods_name` · `unit_price` | 주문 당시 상품명·단가 보관. 이후 상품 정보 변경 시에도 과거 주문 내역 유지 |
| `quantity` | 장바구니 및 주문 상품의 수량 보관. 1 이상만 허용 |
| `total_amount` | 서버에서 주문 당시 단가와 수량으로 계산한 총액 보관 |
| `recipient_name` · `phone` · `postal_code` · `address` · `address_detail` | 주문별 배송 정보 보관. 기본 배송지 별도 저장 제외 |
| `created_at` | 상품 등록 시각·주문 생성 시각을 서버에서 기록 |

`CART_ITEM`에는 `UNIQUE(member_id, goods_id)`를 적용합니다. 같은 회원이 같은 상품을 다시 담으면 기존 항목의 수량을 늘립니다.

[ERD 목록](../README.md) · [개발 코드](../../../apps/m5-store/README.md)
