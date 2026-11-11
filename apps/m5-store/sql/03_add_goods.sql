SET NAMES utf8mb4;
USE store;

START TRANSACTION;

INSERT INTO goods (name, description, price, image_key, active)
SELECT CONCAT('OTTer ', p.product_name), p.description, p.price,
       CONCAT('/images/goods/', p.filename), TRUE
FROM (
    SELECT '텀블러' AS product_name, '뚜껑이 있는 휴대용 텀블러' AS description,
           24000.00 AS price, 'tumbler.jpg' AS filename
    UNION ALL SELECT '볼캡', 'OTTer 로고를 넣은 볼캡', 22000, 'cap.jpg'
    UNION ALL SELECT '반팔 티셔츠', 'OTTer 로고를 넣은 반팔 티셔츠', 29000, 'tshirt.jpg'
    UNION ALL SELECT '후드티', 'OTTer 로고를 넣은 후드티', 49000, 'hoodie.jpg'
    UNION ALL SELECT '양말', '수달 캐릭터 무늬가 들어간 양말', 8000, 'socks.jpg'
    UNION ALL SELECT '파우치', '작은 소지품을 담는 지퍼 파우치', 13000, 'pouch.jpg'
    UNION ALL SELECT '스티커팩', '수달 캐릭터로 꾸민 스티커 세트', 5000, 'stickers.jpg'
    UNION ALL SELECT '노트', '메모하기 좋은 줄 노트', 9000, 'notebook.jpg'
    UNION ALL SELECT '볼펜', 'OTTer 로고를 넣은 검정 볼펜', 4000, 'pen.jpg'
    UNION ALL SELECT '코스터', '수달 캐릭터가 그려진 컵받침', 6500, 'coaster.jpg'
    UNION ALL SELECT '마우스패드', '책상 위에 놓는 수달 마우스패드', 12000, 'mousepad.jpg'
    UNION ALL SELECT '수달 인형', '책상 위에 두기 좋은 수달 인형', 28000, 'plush.jpg'
) AS p
WHERE NOT EXISTS (
    SELECT 1
    FROM goods AS g
    WHERE g.image_key = CONCAT('/images/goods/', p.filename)
);

COMMIT;

SELECT COUNT(*) AS active_goods FROM goods WHERE active = TRUE;