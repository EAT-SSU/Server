UPDATE menu SET name = '숭실라면' WHERE name = '라면' AND restaurant = 'SNACK_CORNER';

INSERT INTO menu (name, name_en, name_ja, name_vi, restaurant, price, is_discontinued, like_count, unlike_count, menu_category_id)
VALUES ('쌀국수', 'Rice Noodles', 'フォー', 'Phở', 'SNACK_CORNER', 5000, false, 0, 0, NULL);

INSERT INTO menu (name, name_en, name_ja, name_vi, restaurant, price, is_discontinued, like_count, unlike_count, menu_category_id)
VALUES ('소떡소떡', 'Sotteok Sotteok', 'ソトックソトック', 'Sotteok Sotteok', 'SNACK_CORNER', 2000, false, 0, 0, NULL);

UPDATE menu SET is_discontinued = true WHERE name = '냉모밀' AND restaurant = 'SNACK_CORNER';
