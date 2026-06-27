-- 단과대학 다국어 데이터 추가
UPDATE college SET name_en = 'Student Council', name_ja = '学生自治会', name_vi = 'Hội Sinh viên' WHERE name_ko = '총학생회';
UPDATE college SET name_en = 'College of AI', name_ja = 'AI学部', name_vi = 'Khoa AI' WHERE name_ko = 'AI대학';
UPDATE college SET name_en = 'College of IT', name_ja = 'IT学部', name_vi = 'Khoa IT' WHERE name_ko = 'IT대학';
UPDATE college SET name_en = 'College of Business Administration', name_ja = '経営学部', name_vi = 'Khoa Quản trị Kinh doanh' WHERE name_ko = '경영대학';
UPDATE college SET name_en = 'College of Economics and International Commerce', name_ja = '経済・国際商学部', name_vi = 'Khoa Kinh tế và Thương mại Quốc tế' WHERE name_ko = '경제통상대학';
UPDATE college SET name_en = 'College of Engineering', name_ja = '工学部', name_vi = 'Khoa Kỹ thuật' WHERE name_ko = '공과대학';
UPDATE college SET name_en = 'College of Law', name_ja = '法学部', name_vi = 'Khoa Luật' WHERE name_ko = '법과대학';
UPDATE college SET name_en = 'College of Social Sciences', name_ja = '社会科学部', name_vi = 'Khoa Khoa học Xã hội' WHERE name_ko = '사회과학대학';
UPDATE college SET name_en = 'College of Humanities', name_ja = '人文学部', name_vi = 'Khoa Nhân văn' WHERE name_ko = '인문대학';
UPDATE college SET name_en = 'College of Natural Sciences', name_ja = '自然科学部', name_vi = 'Khoa Khoa học Tự nhiên' WHERE name_ko = '자연과학대학';
UPDATE college SET name_en = 'School of Liberal Studies', name_ja = 'リベラル・スタディーズ学部', name_vi = 'Khoa Nghiên cứu Tự do' WHERE name_ko = '자유전공학부';
UPDATE college SET name_en = 'Department of Next-Generation Semiconductor', name_ja = '次世代半導体学科', name_vi = 'Khoa Bán dẫn Thế hệ Mới' WHERE name_ko = '차세대반도체학과';
