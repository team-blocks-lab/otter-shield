USE store;

START TRANSACTION;

-- 상품 사진은 이후 프론트엔드에 준비할 파일의 조회 경로
-- 같은 테스트 데이터를 재실행할 때 중복 등록 방지
INSERT INTO goods (name, description, price, image_key, active)
SELECT
    'OTTer 머그컵',
    'OTTer 로고가 들어간 데일리 머그컵',
    15000.00,
    '/images/goods/mug.jpg',
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM goods WHERE name = 'OTTer 머그컵'
);

INSERT INTO goods (name, description, price, image_key, active)
SELECT
    'OTTer 에코백',
    '가볍게 사용할 수 있는 캔버스 에코백',
    18000.00,
    '/images/goods/bag.jpg',
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM goods WHERE name = 'OTTer 에코백'
);

INSERT INTO goods (name, description, price, image_key, active)
SELECT
    'OTTer 키링',
    '가방과 열쇠에 달 수 있는 OTTer 키링',
    7000.00,
    '/images/goods/keyring.jpg',
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM goods WHERE name = 'OTTer 키링'
);

INSERT INTO goods (name, description, price, image_key, active)
SELECT
    'OTTer 판매중지 테스트 상품',
    '공개 목록과 상세 조회에서 제외할 테스트 상품',
    1000.00,
    '/images/goods/inactive.jpg',
    FALSE
WHERE NOT EXISTS (
    SELECT 1 FROM goods WHERE name = 'OTTer 판매중지 테스트 상품'
);

COMMIT;

SELECT goods_id, name, price, active
FROM goods
ORDER BY goods_id;