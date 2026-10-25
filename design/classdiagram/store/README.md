# Store 클래스 다이어그램

상품 조회·장바구니·모의 주문을 처리하는 Store 서비스의 클래스 구조입니다.

## 주요 기능

- 비로그인 상태에서 판매 중인 상품 목록·상세 조회
- OTT와 공통 Redis 세션을 이용한 로그인 유지
- 로그인 회원의 장바구니 상품 추가·수량 변경·삭제
- 장바구니 전체 또는 선택 상품의 모의 주문
- 주문 시 배송 정보 입력과 본인 주문 목록·상세 조회
- 서버에서 주문 금액 계산과 판매 여부·수량·소유자 검증

## 주요 구조

| 구분 | 역할 |
| --- | --- |
| GoodsController · GoodsService | 판매 중인 상품 목록·상세 조회 |
| CartController · CartService | 회원별 장바구니 조회·추가·수량 변경·삭제 |
| OrderController · OrderService | 선택 상품의 모의 주문과 본인 주문 조회 |
| StoreAuthInterceptor · CurrentMemberResolver | 로그인 검사와 세션의 회원 번호 확인 |
| StoreMediaUrlService | 상품 사진 키를 조회용 URL로 변환 |
| GoodsRepository · CartItemRepository · PurchaseOrderRepository | 상품·장바구니·주문의 DB 조회와 저장·삭제 |
| Goods · CartItem · PurchaseOrder · OrderItem | 상품·장바구니·주문 및 주문 상품 정보 관리 |
| Request · Response DTO | 화면과 서버 사이의 요청·응답 데이터 전달 |

회원 번호는 요청 데이터로 받지 않고 세션에서 가져옵니다. 공통 Redis 세션은 Spring Session 설정으로 연결하며, Store에는 별도 회원가입·로그인 클래스를 두지 않습니다.

전체 주문과 선택 주문은 `OrderCreateRequest.cartItemIds`로 처리합니다. 빈 선택, 중복된 항목 ID, 다른 회원의 항목, 판매 중지 상품과 잘못된 수량은 거절합니다. 상품 가격과 주문 총액은 서버에서 계산합니다.

주문 당시 상품명·단가·배송지를 보관하고, 주문 저장과 선택 장바구니 항목 삭제는 하나의 트랜잭션으로 처리합니다. 주문 상품은 `PurchaseOrder.items`를 통해 함께 저장합니다.

## Class Diagram

![Store 클래스 다이어그램](store_ClassDiagram.drawio.png)

[원본 이미지 보기](store_ClassDiagram.drawio.png)

[클래스 다이어그램 목록](../README.md) · [개발 코드](../../../apps/m5-store/README.md)
