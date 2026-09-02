UPDATE college
SET name_ja = 'AI学部',
    name_vi = 'Khoa AI'
WHERE name_ko = 'AI대학';

UPDATE department SET name_ja = '保険数理学', name_vi = 'Khoa học Bảo hiểm' WHERE name_ko LIKE '%보험수리%';
UPDATE department SET name_ja = '産業・情報システム工学', name_vi = 'Kỹ thuật Hệ thống Công nghiệp và Thông tin' WHERE name_ko LIKE '%정보시스템공학%';
UPDATE department SET name_ja = '情報セキュリティ学科', name_vi = 'Khoa An toàn thông tin' WHERE name_ko LIKE '%정보보호%';
