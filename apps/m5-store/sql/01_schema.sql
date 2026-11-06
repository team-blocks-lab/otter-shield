CREATE DATABASE IF NOT EXISTS store
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE store;

-- Store 자체 회원 정보
CREATE TABLE IF NOT EXISTS member (
    member_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    login_id VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (member_id),
    CONSTRAINT uk_store_member_login_id UNIQUE (login_id)
) ENGINE = InnoDB;

-- 상품 정보 및 신규 주문 가능 여부
CREATE TABLE IF NOT EXISTS goods (
    goods_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    image_key VARCHAR(500) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (goods_id),
    CONSTRAINT chk_goods_price CHECK (price >= 0)
) ENGINE = InnoDB;

-- 회원별 상품 수량 보관 및 같은 상품의 중복 행 방지
CREATE TABLE IF NOT EXISTS cart_item (
    cart_item_id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    goods_id BIGINT NOT NULL,
    quantity INT NOT NULL,

    PRIMARY KEY (cart_item_id),
    CONSTRAINT uk_cart_member_goods UNIQUE (member_id, goods_id),
    CONSTRAINT fk_cart_member
        FOREIGN KEY (member_id) REFERENCES member (member_id),
    CONSTRAINT fk_cart_goods
        FOREIGN KEY (goods_id) REFERENCES goods (goods_id),
    CONSTRAINT chk_cart_quantity CHECK (quantity BETWEEN 1 AND 99)
) ENGINE = InnoDB;

-- 모의 주문 및 주문 당시 배송 정보
-- SQL 예약어 ORDER와의 충돌 방지를 위한 테이블명
CREATE TABLE IF NOT EXISTS purchase_order (
    order_id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    total_amount DECIMAL(14, 2) NOT NULL,
    recipient_name VARCHAR(50) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    address_detail VARCHAR(255) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (order_id),
    CONSTRAINT fk_order_member
        FOREIGN KEY (member_id) REFERENCES member (member_id),
    CONSTRAINT chk_order_total CHECK (total_amount >= 0),
    INDEX idx_order_member_created (member_id, created_at, order_id)
) ENGINE = InnoDB;

-- 상품 정보 변경 이후에도 유지할 주문 당시 상품명·단가
CREATE TABLE IF NOT EXISTS order_item (
    order_item_id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    goods_id BIGINT NOT NULL,
    goods_name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    quantity INT NOT NULL,

    PRIMARY KEY (order_item_id),
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES purchase_order (order_id),
    CONSTRAINT fk_order_item_goods
        FOREIGN KEY (goods_id) REFERENCES goods (goods_id),
    CONSTRAINT chk_order_item_price CHECK (unit_price >= 0),
    CONSTRAINT chk_order_item_quantity CHECK (quantity BETWEEN 1 AND 99)
) ENGINE = InnoDB;