UPDATE menu SET name_en = 'Old-school Korean Lunch Box', name_ja = '昔ながらの韓国式弁当', name_vi = 'Cơm hộp Hàn Quốc kiểu xưa' WHERE menu_id = 31;
UPDATE menu SET name_en = 'Chicken Mayo Rice Bowl', name_ja = 'チキンマヨ丼', name_vi = 'Cơm gà sốt mayonnaise' WHERE menu_id = 955;
UPDATE menu SET name_en = 'Soongsil Gimbap', name_ja = '崇実キンパ', name_vi = 'Soongsil Gimbap' WHERE menu_id = 2251;
UPDATE menu SET name_en = 'Soongsil Ramyeon', name_ja = '崇実ラーメン', name_vi = 'Soongsil Ramyeon' WHERE menu_id = 37;
UPDATE menu SET name_en = 'Ramyeon + Half Roll of Gimbap', name_ja = 'ラーメン＋キンパ半本', name_vi = 'Ramyeon + nửa cuộn gimbap' WHERE menu_id = 51;
UPDATE menu SET name_en = 'Tteokbokki', name_ja = 'トッポッキ', name_vi = 'Tteokbokki' WHERE menu_id = 20;
UPDATE menu SET name_en = 'Rabokki', name_ja = 'ラッポッキ', name_vi = 'Rabokki' WHERE menu_id = 46;
UPDATE menu SET name_en = 'Pasta Salad', name_ja = 'サラダパスタ', name_vi = 'Salad mì Ý' WHERE menu_id = 2;
UPDATE menu SET name_en = 'Balanced Macro Salad', name_ja = '栄養バランスサラダ', name_vi = 'Salad cân bằng dinh dưỡng' WHERE menu_id = 3;

UPDATE menu
SET name_en = 'Bowl of Rice', name_ja = 'ご飯一杯', name_vi = 'Cơm trắng', price = 1000, is_discontinued = false
WHERE menu_id = 32;

UPDATE menu
SET name = '냉모밀+갈비만두2개',
    name_en = 'Cold Soba + 2 Galbi Mandu',
    name_ja = '冷やしそば＋カルビ餃子2個',
    name_vi = 'Mì soba lạnh + 2 bánh mandu nhân thịt galbi',
    price = 5000,
    menu_category_id = 6,
    is_discontinued = false
WHERE menu_id = 3180;

UPDATE menu
SET name = '치즈떡라면',
    name_en = 'Cheese and Rice Cake Ramyeon',
    name_ja = 'チーズ＆トック入りラーメン',
    name_vi = 'Mì ramyeon phô mai và bánh gạo',
    price = 4300,
    menu_category_id = 5,
    is_discontinued = false
WHERE menu_id = 2216;

INSERT INTO menu (name, name_en, name_ja, name_vi, restaurant, price, is_discontinued, like_count, unlike_count, menu_category_id)
VALUES ('냉모밀+갈비만두6개', 'Cold Soba + 6 Galbi Mandu', '冷やしそば＋カルビ餃子6個', 'Mì soba lạnh + 6 bánh mandu nhân thịt galbi', 'SNACK_CORNER', 6000, false, 0, 0, 6),
       ('잔치국수+갈비만두2개', 'Janchi Guksu (Korean Noodle Soup) + 2 Galbi Mandu', 'チャンチグクス（韓国式温麺）＋カルビ餃子2個', 'Mì nước Janchi Guksu kiểu Hàn + 2 bánh mandu nhân thịt galbi', 'SNACK_CORNER', 5000, false, 0, 0, 6),
       ('잔치국수+갈비만두6개', 'Janchi Guksu (Korean Noodle Soup) + 6 Galbi Mandu', 'チャンチグクス（韓国式温麺）＋カルビ餃子6個', 'Mì nước Janchi Guksu kiểu Hàn + 6 bánh mandu nhân thịt galbi', 'SNACK_CORNER', 6000, false, 0, 0, 6),
       ('비빔국수+갈비만두2개', 'Bibim Guksu (Spicy Mixed Noodles) + 2 Galbi Mandu', 'ビビングクス（韓国風ピリ辛混ぜ麺）＋カルビ餃子2個', 'Mì trộn cay Bibim Guksu + 2 bánh mandu nhân thịt galbi', 'SNACK_CORNER', 5000, false, 0, 0, 6),
       ('비빔국수+갈비만두6개', 'Bibim Guksu (Spicy Mixed Noodles) + 6 Galbi Mandu', 'ビビングクス（韓国風ピリ辛混ぜ麺）＋カルビ餃子6個', 'Mì trộn cay Bibim Guksu + 6 bánh mandu nhân thịt galbi', 'SNACK_CORNER', 6000, false, 0, 0, 6),
       ('[포장] 샐러드파스타', '[Takeout] Pasta Salad', '[テイクアウト] サラダパスタ', '[Mang đi] Salad mì Ý', 'SNACK_CORNER', 8000, false, 0, 0, 9),
       ('[포장] 탄단지샐러드', '[Takeout] Balanced Macro Salad', '[テイクアウト] 栄養バランスサラダ', '[Mang đi] Salad cân bằng dinh dưỡng', 'SNACK_CORNER', 8000, false, 0, 0, 9);

UPDATE menu
SET is_discontinued = true
WHERE menu_id IN (2376, 38, 4, 6, 7, 8, 2213, 2208, 2209, 24, 3178, 3179, 3920, 4656, 4657);
