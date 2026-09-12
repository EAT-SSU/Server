-- V25: 2026-2학기 제휴 갱신 + 2026 동연제 한정 제휴 추가
-- 생성: 26-2 제휴 정리 CSV 기준 자동 생성 (매장명 표기/좌표/restaurantType 정합화 + 실제 college 정식명칭 매칭 포함)

-- 1) 이번 갱신 대상 단과대 College row 존재 보장 (실제로는 축제만 신규, 나머지는 이미 존재)
INSERT INTO college (name_ko)
SELECT 'AI대학' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = 'AI대학');
INSERT INTO college (name_ko)
SELECT 'IT대학' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = 'IT대학');
INSERT INTO college (name_ko)
SELECT '경영대학' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = '경영대학');
INSERT INTO college (name_ko)
SELECT '경제통상대학' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = '경제통상대학');
INSERT INTO college (name_ko)
SELECT '공과대학' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = '공과대학');
INSERT INTO college (name_ko)
SELECT '법과대학' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = '법과대학');
INSERT INTO college (name_ko)
SELECT '사회과학대학' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = '사회과학대학');
INSERT INTO college (name_ko)
SELECT '인문대학' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = '인문대학');
INSERT INTO college (name_ko)
SELECT '자연과학대학' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = '자연과학대학');
INSERT INTO college (name_ko)
SELECT '자유전공학부' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = '자유전공학부');
INSERT INTO college (name_ko)
SELECT '총학생회' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = '총학생회');
INSERT INTO college (name_ko)
SELECT '축제' WHERE NOT EXISTS (SELECT 1 FROM college WHERE name_ko = '축제');

-- 2) 이번 CSV에 포함된 단과대(축제 제외)의 기존 NORMAL 제휴 일괄 만료
UPDATE partnership p
JOIN college c ON c.college_id = p.partnership_college_id
SET p.end_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
WHERE c.name_ko IN ('AI대학', 'IT대학', '경영대학', '경제통상대학', '공과대학', '법과대학', '사회과학대학', '인문대학', '자연과학대학', '자유전공학부', '총학생회')
  AND p.period_type = 'NORMAL'
  AND p.end_date >= CURDATE();

-- 3) 신규 매장(partnership_restaurant) 추가 (기존 매장명과 일치하면 재사용, 신규일 때만 INSERT)
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9570505, 37.4950904, '면식당'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '면식당');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9567297, 37.4951458, '샹츠마라'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '샹츠마라');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9566531, 37.4951098, '지지고'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '지지고');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9566592, 37.4949404, '먹돼지'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '먹돼지');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.958571, 37.494738, '파라다이스'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '파라다이스');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.957536, 37.4946855, '왕돈까스왕냉면'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '왕돈까스왕냉면');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9570332, 37.4949219, '신의주 찹쌀순대'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '신의주 찹쌀순대');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9580199, 37.4949627, '포케올데이'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '포케올데이');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.95642, 37.495082, '삼봉버거'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '삼봉버거');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9572008, 37.4946934, '숯가마 바베큐 치킨'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '숯가마 바베큐 치킨');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.958319, 37.494937, '황궁쟁반짜장'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '황궁쟁반짜장');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9566364, 37.4948253, '멘동'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '멘동');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.956443, 37.494862, '핵밥'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '핵밥');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9558406, 37.4953194, '슬로우캘리'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '슬로우캘리');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9561376, 37.4938262, '고추동 제면소'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '고추동 제면소');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9575572, 37.4948253, '코웍 비스트로'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '코웍 비스트로');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9564154, 37.4948391, '밀플랜비'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '밀플랜비');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9571, 37.49512, '은화수식당'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '은화수식당');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.956932, 37.4947606, '더진국'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '더진국');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.956469, 37.4950015, '불백스테이션'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '불백스테이션');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.9572999, 37.4948761, '인쌩맥주'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '인쌩맥주');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.9573923, 37.4946689, '역전할머니맥주'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '역전할머니맥주');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.9567297, 37.4951458, '사랑과 평화'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '사랑과 평화');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.956856, 37.494954, '젠사이야'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '젠사이야');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.9559529, 37.4952506, '블루힐'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '블루힐');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.9555115, 37.4953563, '빽다방'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '빽다방');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.9542227, 37.4949956, '에브어이브'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '에브어이브');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.9564876, 37.4952029, '카페 봄봄'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '카페 봄봄');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.95769, 37.494356, 'eea cafe'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = 'eea cafe');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.9549874, 37.4952765, '놀숲'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '놀숲');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.9563371, 37.4947153, '히어로 보드게임 카페'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '히어로 보드게임 카페');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.957666, 37.494941, '에이플 PC방'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '에이플 PC방');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.956386, 37.495213, '블라블라 코인노래방'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '블라블라 코인노래방');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9552024, 37.4953794, '취향'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '취향');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.957807, 37.495008, '손칼국수'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '손칼국수');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9533875, 37.4969431, '피자스쿨'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '피자스쿨');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9520881, 37.4987842, '광어삼촌'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '광어삼촌');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.953541, 37.496939, '새벽집 24'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '새벽집 24');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.953541, 37.496939, '전설의 왕만두'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '전설의 왕만두');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.9570925, 37.494662, '상도로 3가'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '상도로 3가');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.953024, 37.4958719, '파동추야'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '파동추야');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.955617, 37.495345, '공차'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '공차');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.957409, 37.494802, '긱스타 PC방'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '긱스타 PC방');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9534144, 37.4957791, '상도동 솥뚜껑'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '상도동 솥뚜껑');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.95275, 37.497968, '맛닭꼬'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '맛닭꼬');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9562643, 37.4950667, '부리또집'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '부리또집');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9551735, 37.4954091, '리얼후라이'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '리얼후라이');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9563299, 37.4949216, '크라이치즈버거'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '크라이치즈버거');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.956364, 37.494455, '아리랑컵밥'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '아리랑컵밥');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.955839, 37.4952956, '가치'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '가치');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.9566154, 37.4947059, '씨밤'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '씨밤');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9605, 37.4946, '미쳐버린 파닭'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '미쳐버린 파닭');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.956775, 37.4949, '미태리'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '미태리');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9570515, 37.4946427, '논두렁갈비'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '논두렁갈비');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.952827, 37.497889, '황새골'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '황새골');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9532541, 37.4960305, '불타는 소금구이'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '불타는 소금구이');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.9560906, 37.4952251, '28청춘호프포차'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '28청춘호프포차');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.9533784, 37.4972209, '요아정'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '요아정');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.9533116, 37.4962773, '메가커피'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '메가커피');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.957403, 37.494689, 'BHC 치킨'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = 'BHC 치킨');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.9535159, 37.4794647, '와룸 보드게임카페 서울대입구역점'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '와룸 보드게임카페 서울대입구역점');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9524353, 37.4987291, '나비루'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '나비루');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 126.9532402, 37.4960303, '블랙&조이커피'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '블랙&조이커피');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'CAFE', 127.007912, 37.5780747, '산1-1'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '산1-1');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.953733, 37.495719, '교촌치킨'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '교촌치킨');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'RESTAURANT', 126.9517, 37.49928, '다성반점'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '다성반점');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.9572, 37.49466, '짚동가리쌩주'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '짚동가리쌩주');
INSERT INTO partnership_restaurant (restaurant_type, longitude, latitude, store_name_ko)
SELECT 'PUB', 126.957, 37.49492, '엉클호프'
WHERE NOT EXISTS (SELECT 1 FROM partnership_restaurant WHERE store_name_ko = '엉클호프');

-- 4) 26-2학기 제휴 + 2026 동연제 한정 제휴 INSERT
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이하 음료 1개 / 3~4인 음료 2개 제공', 'For groups of 1–2, get 1 free drink. For groups of 3–4, get 2 free drinks.', '1〜2人でドリンク1杯を無料提供。
 3〜4人で2 ドリンクを無料提供。', 'Nhóm 1–2 người được tặng 1 đồ uống.
 Nhóm 3–4 người được tặng 2 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '면식당' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '30,000원 이상 구매 시(영수증 3개 이하 합산 가능) 빙홍차 또는 차파이, 유토우 3p 제공', 'For purchases of 30,000 KRW or more, choose one: Bing Hong Cha, Cha Pai, or 3 pieces of Youtiao. Up to 3 receipts may be combined.', '30,000ウォン以上の購入で、油条3個・茶π・冰紅茶のうち1つを選択。レシートは最大3枚まで合算可能。
 レシートは最大3枚まで合算可能。', 'Đơn mua từ 30.000 KRW trở lên được chọn 1 trong các món: 3 miếng quẩy Youtiao, Cha Pai hoặc Bing Hong Cha. Có thể gộp tối đa 3 hóa đơn.
 Có thể gộp tối đa 3 hóa đơn.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '샹츠마라' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '20,000원 이상 주문 시 음료 2개 제공
30,000원 이상 주문 시 모든 메뉴 세트로 변경', 'For orders of 20,000 KRW or more, get 2 free drinks. For orders of 30,000 KRW or more, upgrade all menu items to set menus.', '20,000ウォン以上の注文でドリンク2杯を無料提供。
 30,000ウォン以上の注文で全メニューをセットメニューに変更。', 'Đơn từ 20.000 KRW trở lên được tặng 2 đồ uống.
 Đơn từ 30.000 KRW trở lên được nâng toàn bộ menu thành set menu.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '지지고' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '구이류 주문 시 카카오페이/토스페이/계좌이체 시 5% 할인', '5% off when paying with KakaoPay, Toss Pay, or bank transfer for grilled dishes.', '焼き物注文時、KakaoPay・Toss Pay・口座振込決済で5%割引。', 'Giảm 5% món nướng khi thanh toán bằng KakaoPay, Toss Pay hoặc chuyển khoản.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '먹돼지' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 방문 시 음료 1개 제공', 'Free drink for groups of 4 or more.', '4人以上の来店でドリンクを無料提供。', 'Nhóm từ 4 người trở lên được tặng đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '파라다이스' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3인 이상 방문 및 20,000원 이상 주문 시 음료 1개 제공', 'Free drink for groups of 3 or more with orders of 20,000 KRW or more.', '3人以上の来店かつ20,000ウォン以上の注文でドリンク1杯を無料提供。', 'Nhóm từ 3 người trở lên và đơn từ 20.000 KRW trở lên được tặng 1 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '왕돈까스왕냉면' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 주문 시 음료 1개 제공 / 4인 이상 방문하여 50,000원 이상 주문 시 소주 1병 제공', 'For groups of 4 or more, get 1 free drink. Or, for groups of 4 or more spending 50,000 KRW or more, get 1 free bottle of soju.', '4人以上でドリンク1杯を無料提供。
 また、4人以上で50,000ウォン以上利用時、焼酎1本を無料提供。', 'Nhóm từ 4 người trở lên được tặng 1 đồ uống.
 Hoặc nhóm từ 4 người trở lên chi từ 50.000 KRW trở lên được tặng 1 chai soju.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '신의주 찹쌀순대' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '메인 메뉴 주문 시 닭가슴살 또는 훈제오리 토핑 30g 추가', 'Add 30g of chicken breast or smoked duck topping when ordering a main menu item.', 'メインメニュー注文時、鶏むね肉または燻製鴨トッピング30gを追加提供。', 'Khi gọi món chính, được thêm 30g topping ức gà hoặc vịt hun khói.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '포케올데이' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3인당 음료 1개 제공', '1 free drink for every 3 people.', '3人ごとにドリンク1本無料。', 'Tặng 1 đồ uống cho mỗi 3 người.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '삼봉버거' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '40,000원 이상 구매 시 소주 1병 제공', 'Free 1 bottle of soju for purchases of 40,000 KRW or more.', '40,000ウォン以上の購入で焼酎1本を無料提供。', 'Mua từ 40.000 KRW trở lên được tặng 1 chai soju.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '숯가마 바베큐 치킨' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 방문 시 군만두 무료 제공', 'Free fried dumplings for groups of 4 or more.', '4人以上の来店で焼き餃子を無料提供。', 'Nhóm từ 4 người trở lên được tặng mandu chiên.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '황궁쟁반짜장' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이상 주문 시 음료수 1개 제공 (2인당 음료 1개 제공)', '1 free drink for every 2 people.', '1人1メニュー注文時、2人ごとにドリンク1本無料。例：4人の場合は2本。', 'Khi mỗi người gọi 1 món, tặng 1 đồ uống cho mỗi 2 người. Ví dụ: 4 người được 2 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '멘동' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 주문 시 음료 1개 제공 / 3인 이상 주문 시 음료 2개 제공', 'For groups of 3, get 1 free drink. For groups of 4 or more, get 2 free drinks.', '3人でドリンク1杯を無料提供。
 4人以上で2 ドリンクを無料提供。', 'Nhóm 3 người được tặng 1 đồ uống.
 Nhóm từ 4 người trở lên được tặng 2 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '핵밥' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이상 주문 시 음료 1개 제공', 'Free drink for orders of 2 or more people.', '2人以上の注文でドリンク1杯を無料提供。', 'Đơn từ 2 người trở lên được tặng 1 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '슬로우캘리' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1인 1메뉴 주문 시 테이블당 음료 1개 제공', 'Free drink per table when each person orders 1 menu item. Excludes lunch hours from 12 PM to 2 PM and dinner hours from 5 PM to 7 PM.', '1人1メニュー注文時、テーブルごとにドリンク1本を提供。ランチ12〜14時、ディナー17〜19時は対象外。', 'Tặng 1 đồ uống cho mỗi bàn khi mỗi người gọi 1 món. Không áp dụng giờ trưa 12 PM–2 PM và giờ tối 5 PM–7 PM.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '고추동 제면소' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '닭갈비 2인 이상 주문 시 테이블당 치즈사리 1개 제공', 'Free cheese add-on per table when ordering 2 or more servings of Dakgalbi.', 'タッカルビ2人前以上注文時、テーブルごとにチーズ追加を無料提供。', 'Khi gọi từ 2 phần dakgalbi trở lên, mỗi bàn được tặng phần phô mai thêm.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '코웍 비스트로' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '브리또 주문 시 토핑 추가 (베이컨 제외)', 'Free topping add-on for burrito orders, excluding bacon.', 'ブリトー注文時、ベーコンを除くトッピング1つ無料。', 'Gọi burrito được thêm 1 topping miễn phí, trừ bacon.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '밀플랜비' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '메인 메뉴 2개당 음료수 1개 제공', '1 free drink for every 2 main menu items.', 'メインメニュー2品ごとにドリンク1杯を無料提供。', 'Cứ mỗi 2 món chính được tặng 1 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '은화수식당' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 주문 시 음료수 1캔 제공
5만원 이상 주문 시 소주 1병 or 음료수 1캔 제공', 'For groups of 4 or more, get 1 free canned drink. For orders of 50,000 KRW or more, choose one: 1 bottle of soju or 1 canned drink.', '4人以上の注文でドリンク1缶を無料提供。
50,000ウォン以上の注文で、焼酎1本またはドリンク1缶のうち1つを無料提供。', 'Nhóm từ 4 người trở lên được tặng 1 lon nước ngọt.
Đơn từ 50.000 KRW trở lên được chọn 1 trong 2: 1 chai soju hoặc 1 lon nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '더진국' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인당 세트메뉴 주문 시 음료수 1캔 제공
단품 메뉴 3개 주문 시 음료수 1캔 제공', 'Get 1 free canned drink when ordering set menus for 2 people. Get 1 free canned drink when ordering 3 à la carte items.', 'セットメニューを2人分注文すると、ドリンク1缶を無料提供。
単品メニューを3品注文すると、ドリンク1缶を無料提供。', 'Khi gọi set menu cho 2 người, được tặng 1 lon nước ngọt.
Khi gọi 3 món lẻ, được tặng 1 lon nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '불백스테이션' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '메인 메뉴 2개 이상 주문 시 현금 결제 또는 계좌이체 5% 할인', '5% off when ordering 2 or more main menu items and paying by cash or bank transfer.', 'メインメニュー2品以上注文し、現金または口座振込で決済時5%割引。', 'Gọi từ 2 món chính trở lên và thanh toán bằng tiền mặt hoặc chuyển khoản được giảm 5%.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '인쌩맥주' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '50,000원 이상 구매 시 참이슬 1병 or 수제 소시지 제공', 'For purchases of 50,000 KRW or more, choose one: 1 bottle of soju or Garlic Butter Potato.', '50,000ウォン以上で、焼酎1本またはガーリックバターポテトのうち1つを選択。', 'Từ 50.000 KRW trở lên được chọn 1 trong các món: 1 chai soju hoặc khoai tây bơ tỏi.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '역전할머니맥주' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '40,000원 이상 주문 시 현금 결제 또는 계좌이체 5% 할인', 'Free French Fries for orders of 40,000 KRW or more.', '40,000ウォン以上の注文でフライドポテトを無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng khoai tây chiên.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '사랑과 평화' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '50,000원 이상 주문 시 소주 1병 제공', 'Free 1 bottle of soju for orders of 50,000 KRW or more.', '50,000ウォン以上の注文で焼酎1本を無料提供。', 'Đơn từ 50.000 KRW trở lên được tặng 1 chai soju.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '젠사이야' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 테이블 주문 시 음료 1캔 제공', '1 free canned drink for tables of 4 or more.', '4人以上のテーブル注文でドリンク1缶を無料提供。', 'Bàn từ 4 người trở lên được tặng 1 lon nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블루힐' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '수요일에 음료 2잔 주문 시 1잔에 샷 추가', 'Free 1 Iced Tea when ordering 3 drinks on Fridays.', '金曜日にドリンク3杯注文時、アイスティー1杯を無料提供。', 'Thứ Sáu gọi 3 đồ uống được tặng 1 trà đá.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '빽다방' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '전 메뉴 10% 할인 (세트 메뉴 제외)', '10% off all menu items, excluding set menus.', 'セットメニューを除く全メニュー10%割引。', 'Giảm 10% toàn bộ menu, không gồm set menu.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '에브어이브' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '빅 아이스아메리카노 2잔 주문 시 한 잔에 샷 추가 
빅 아이스아메리카노 + 아이스티 주문 시 한 잔에 샷 추가', 'Free extra shot in 1 drink when ordering 2 Big Iced Americanos or 2 Big Iced Teas.', 'ビッグサイズアイスアメリカーノ2杯またはビッグサイズアイスティー2杯注文時、1杯にショット1回無料追加。', 'Gọi 2 Americano đá cỡ lớn hoặc 2 trà đá cỡ lớn được thêm 1 shot miễn phí vào 1 ly.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '카페 봄봄' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '음료 10% 할인', '10% off drinks.', 'ドリンク10%割引。', 'Giảm 10% đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = 'eea cafe' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT 'Set a, b, c 요금제를 청소년 요금제로 이용 가능', 'Set A, B, and C available at youth rates.', 'Set A・B・Cは青少年料金で利用可能。', 'Set A, B, C áp dụng giá thanh thiếu niên.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '놀숲' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '게임비 15% 할인
빙수 10% 할인 (계좌이체 주문시) 
(카운터 결제 필요)', '15% off.', '15%割引。', 'Giảm 15%.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '히어로 보드게임 카페' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1만원(현금, 계좌이체) 계산 시 10시간 + 음료 1잔 제공', 'Pay 10,000 KRW by cash or bank transfer and get 10 hours of PC use plus 1 drink.', '10,000ウォンを現金または口座振込で支払うと、PC利用10時間とドリンク1杯を提供。', 'Thanh toán 10.000 KRW bằng tiền mặt hoặc chuyển khoản được sử dụng PC trong 10 giờ và nhận 1 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '에이플 PC방' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1만원 이상 현금 결제, 계좌이체 시 5곡 추가 증정 (카드결제 불가)', 'Get 5 extra songs when spending 10,000 KRW or more and paying by cash or bank transfer. Card payments are not accepted.', '10,000ウォン以上を現金または口座振込で支払うと、5曲追加。カード決済不可。', 'Thanh toán từ 10.000 KRW trở lên bằng tiền mặt hoặc chuyển khoản được tặng thêm 5 bài hát. Không chấp nhận thanh toán bằng thẻ.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블라블라 코인노래방' AND c.name_ko = 'IT대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3만원 이상 주문 시, 유토우 3ps or 차파이(음료) or 빙홍차(음료) 
*당일 영수증에 한하여 가능, 학생증 필수 지참', NULL, NULL, NULL, '2026-09-01', '2026-12-20', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '샹츠마라' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1. 4인 이상 방문 시 음료 1병 지급 
2. 5만원 이상 결제 시 소주 1병 또는 음료 1병 제공 
*1, 2번 항목 중복 적용 불가', 'For groups of 4 or more, get 1 free bottled soft drink. For payments of 50,000 KRW or more, get 1 free bottle of soju or 1 bottled soft drink. Benefits cannot be combined.', '4人以上の来店でボトルドリンク1本を無料提供。
 50,000ウォン以上の決済で焼酎1本またはボトルドリンク1本を無料提供。
 1番と2番の特典は併用不可。', 'Nhóm từ 4 người trở lên được tặng 1 chai nước ngọt.
 Thanh toán từ 50.000 KRW trở lên được tặng 1 chai soju hoặc 1 chai nước ngọt.
 Không áp dụng đồng thời ưu đãi 1 và 2.', '2026-03-03', '2027-02-28', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '신의주 찹쌀순대' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2만원 이상 주문 시 음료 1캔 제공', 'Free 1 canned soft drink for orders of 20,000 KRW or more.', '20,000ウォン以上の注文で缶ドリンク1本を無料提供。', 'Đơn từ 20.000 KRW trở lên được tặng 1 lon nước ngọt.', '2026-03-03', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '왕돈까스왕냉면' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 주문 시 음료 1캔 or 군만두 4개 제공', 'For groups of 4 or more, choose one: 1 canned soft drink or 4 fried dumplings.', '4人以上の来店で、1 缶飲料または4 焼き餃子から1つ選択。', 'Nhóm từ 4 người trở lên chọn 1 ưu đãi: 1 nước ngọt đóng lon hoặc 4 mandu chiên.', '2026-03-03', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '취향' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '브리또 주문 시 토핑 1개 무료 (베이컨 제외)', 'Free topping add-on for burrito orders, excluding bacon.', 'ブリトー注文時、ベーコンを除くトッピング1つ無料。', 'Gọi burrito được thêm 1 topping miễn phí, trừ bacon.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '밀플랜비' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2만원 이상 주문 시 음료 2개 제공
3만원 이상 주문 시 단품을 세트로 제공', 'For orders of 20,000 KRW or more, get 2 free drinks. For orders of 30,000 KRW or more, upgrade all single items to set menus.', '20,000ウォン以上の注文でドリンク2杯を無料提供。
 30,000ウォン以上の注文で単品をセットメニューに変更。', 'Đơn từ 20.000 KRW trở lên được tặng 2 đồ uống.
 Đơn từ 30.000 KRW trở lên được nâng món lẻ thành set menu.', '2026-03-03', '2027-02-28', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '지지고' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '현금/계좌이체 시 메뉴당 500원 할인', '500 KRW off per menu item when paying by cash or bank transfer.', '現金または口座振込決済時、メニュー1品につき500ウォン割引。', 'Giảm 500 KRW cho mỗi món khi thanh toán bằng tiền mặt hoặc chuyển khoản.', '2026-03-01', '2027-02-28', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '손칼국수' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '테이블 당 닭갈비 2인분 이상일 시 치즈사리 추가 제공', NULL, NULL, NULL, '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '코웍 비스트로' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '구이류 주문 시, 카카오페이, 계좌이체(현금), 토스 결제 시 5% 할인', '5% off grilled dishes when paying with KakaoPay, Toss, cash, or bank transfer.', '焼き物注文時、KakaoPay・Toss・現金・口座振込決済で5%割引。', 'Giảm 5% món nướng khi thanh toán bằng KakaoPay, Toss, tiền mặt hoặc chuyển khoản.', '2026-03-03', '2027-02-28', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '먹돼지' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1인 1메뉴 주문 시 2인당 음료 1개 (ex. 4인일 경우 음료 2개 제공)', NULL, NULL, NULL, '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '멘동' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이하 음료 1캔, 3~4인 음료 2캔 제공', 'For groups of 1–2, get 1 canned soft drink. For groups of 3–4, get 2 canned soft drinks.', '1〜2人で缶ドリンク1本を無料提供。
 3〜4人で2 缶ドリンクを無料提供。', 'Nhóm 1–2 người được tặng 1 lon nước ngọt.
 Nhóm 3–4 người được tặng 2 nước ngọt đóng lon.', '2026-03-01', '2027-02-28', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '면식당' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '주문금액 30,000원 당 펩시 제로 1.25L 제공', 'Free 1.25L Pepsi Zero for every 30,000 KRW spent.', '30,000ウォンごとの利用で1.25L ペプシゼロを無料提供。', 'Tặng 1.25L Pepsi Zero cho mỗi 30.000 KRW chi tiêu.', '2026-02-25', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '피자스쿨' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만원 이상 주문 시 음료수 또는 소주 1병 제공', 'Free bottled soft drink or 1 bottle of soju for orders of 50,000 KRW or more.', '50,000ウォン以上の注文でボトルドリンクまたは焼酎1本を無料提供。', 'Đơn từ 50.000 KRW trở lên được tặng nước ngọt đóng chai hoặc 1 chai soju.', '2026-02-25', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '광어삼촌' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '메인토핑 주문 시, 닭가슴살 or 훈제오리 토핑 30g 추가 제공
*사이드 메뉴 제외', 'Get an extra 30g of chicken breast or smoked duck topping when ordering a main topping. Side menu items are excluded.', 'メイントッピング注文時、鶏むね肉または燻製鴨のトッピングを30g追加提供。サイドメニューは外対象。', 'Khi gọi topping chính, được thêm 30g topping ức gà hoặc vịt hun khói. Không áp dụng cho món phụ.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '포케올데이' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 방문 시 음료 1캔 제공', '1 free canned drink for groups of 4 or more.', '4人以上の来店でドリンク1缶を無料提供。', 'Nhóm từ 4 người trở lên được tặng 1 lon nước ngọt.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '더진국' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '뼈해장국 주문 시 11,000원 -> 10,000원으로 할인', 'Ppyeo Haejangguk is discounted from 11,000 KRW to 10,000 KRW.', 'ピョヘジャングクを11,000ウォンから10,000ウォンに割引。', 'Món canh xương heo giải rượu được giảm từ 11.000 KRW còn 10.000 KRW.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '새벽집 24' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '왕만두, 찐빵, 교자 6,000원 -> 5,000원으로 할인', 'King-size mandu, steamed buns, and gyoza are discounted from 6,000 KRW to 5,000 KRW.', 'ワンマンドゥ・チンパン・餃子を6,000ウォンから5,000ウォンに割引。', 'Mandu cỡ lớn, bánh bao hấp và gyoza được giảm từ 6.000 KRW còn 5.000 KRW.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '전설의 왕만두' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '현금 또는 계좌이체 시 5% 할인', '5% off when paying by cash or bank transfer.', '現金または口座振込決済時、5%割引。', 'Giảm 5% khi thanh toán bằng tiền mặt hoặc chuyển khoản.', '2026-02-25', '2027-02-28', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도로 3가' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '40,000원 이상 주문 시 음료 1캔 증정', '1 free canned drink for orders of 40,000 KRW or more.', '40,000ウォン以上の注文でドリンク1缶を無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng 1 lon nước ngọt.', '2026-02-25', '2026-06-30', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '파동추야' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '70,000원 이상 현금 결제 시 김치볶음밥 또는 계란말이 제공 (단체 제외)', 'For cash payments of 70,000 KRW or more, choose one: Kimchi Fried Rice or Rolled Omelet. Groups excluded.', '70,000ウォン以上の現金決済で、キムチチャーハンまたは卵焼きのうち1つを提供。団体は対象外。', 'Thanh toán tiền mặt từ 70.000 KRW trở lên được chọn 1 món: cơm chiên kimchi hoặc trứng cuộn. Không áp dụng cho đoàn.', '2026-03-03', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블루힐' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만원 이상 주문 시, 갈릭빠다 포테이토 or 수제 소시지 중 택1 제공
*갈릭빠다 포테이토는 정량보다 적은 양', 'For orders of 50,000 KRW or more, choose one: Garlic Butter Potatoes or Handmade Sausage. The Garlic Butter Potatoes are served in a smaller-than-standard portion.', '50,000ウォン以上の注文で、ガーリックバターポテトまたは手作りソーセージのうち1つを提供。ガーリックバターポテトは通常より少なめの量で提供。', 'Đơn từ 50.000 KRW trở lên được chọn 1 trong 2 món: khoai tây bơ tỏi hoặc xúc xích thủ công. Phần khoai tây bơ tỏi có khẩu phần nhỏ hơn thông thường.', '2026-02-07', '2026-06-30', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '역전할머니맥주' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '아메리카노 혹은 아이스티 빅사이즈 도합 2잔 주문 시 1잔 샷추가 (빅사이즈)', 'Free extra shot in one drink when ordering a total of 2 Big Americanos and/or Big Iced Teas.', 'ビッグサイズアメリカーノまたはビッグサイズアイスティーを合計2杯注文時、1杯にショット1回無料追加。', 'Gọi tổng cộng 2 ly Americano cỡ lớn và/hoặc trà đá cỡ lớn được thêm 1 shot miễn phí vào 1 ly.', '2026-03-03', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '카페 봄봄' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '방문 결제에 한하여 세트메뉴 제외 10% 할인', '10% off all menu items, excluding set menus, for in-store payments only.', '店頭決済時のみ、セットメニューを除く全メニュー10%割引。', 'Giảm 10% toàn bộ menu, không gồm set menu, chỉ áp dụng khi thanh toán tại cửa hàng.', '2026-02-07', '2027-02-28', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '에브어이브' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '모든 음료 주문 시, 타피오카펄 무료 추가 (폼, 다른 토핑류 X)
점보 사이즈는 펄 라지 양으로 제공
*학생증 증명 필수 (동행까지 적용 가능)
*중복 혜택 적용 불가 (공차데이, 통신사 할인폭이 큰 이벤트의 경우)', 'Free tapioca pearls with any drink order. Foam and other toppings are excluded.
For jumbo drinks, the amount of pearls provided will be the same as a large size.
Student ID verification is required. The benefit may also be applied to companions.
Cannot be combined with other promotions, including Gong Cha Day or mobile carrier promotions offering higher discounts.', 'すべてのドリンク注文時、タピオカパールを無料追加。フォームやその他のトッピングは対象外。
ジャンボサイズは、ラージサイズ分のパールを提供。
学生証の提示必須。同伴者にも適用可能。
ゴンチャデーや通信会社の割引率が高いキャンペーンなど、他の特典との併用不可。', 'Khi gọi bất kỳ đồ uống nào, được thêm trân châu tapioca miễn phí. Không áp dụng cho lớp foam hoặc các loại topping khác.
Với size Jumbo, lượng trân châu được tặng tương đương size Large.
Bắt buộc xuất trình thẻ sinh viên; ưu đãi có thể áp dụng cho cả người đi cùng.
Không áp dụng đồng thời với ưu đãi khác, bao gồm Gong Cha Day hoặc chương trình giảm giá cao của nhà mạng.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '공차' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5,000원 충전 시 탄산음료 한 잔, 10,000원 충전 시 카페음료 한 잔 제공
*카페 음료는 아이스 아메리카노, 아이스티, 카페라떼, 카페모카, 체리에이드, 레몬에이드, 자몽에이드, 청포도에이드 중 택1
*위 목록 외 음료는 문의 시 여건에 따라 제공 가능', 'Get a free soda when adding 5,000 KRW of credit. Get a free café drink when adding 10,000 KRW of credit.
For the café drink benefit, choose one: Iced Americano, iced tea, café latte, café mocha, cherry ade, lemon ade, grapefruit ade, or green grape ade. Drinks not listed above may be available upon request, subject to availability.', '5,000ウォンチャージで炭酸飲料を無料提供。
10,000ウォンチャージでカフェドリンクを無料提供。
カフェドリンクは、アイスアメリカーノ、アイスティー、カフェラテ、カフェモカ、チェリーエイド、レモンエイド、グレープフルーツエイド、青ぶどうエイドの中から1つ選択。上記以外のドリンクは、問い合わせ時の状況により提供可能。', 'Nạp 5.000 KRW được tặng soda.
Nạp 10.000 KRW được tặng đồ uống cà phê.
Đồ uống được chọn 1 trong các món: Americano đá, trà đá, café latte, café mocha, ade anh đào, ade chanh, ade bưởi hoặc ade nho xanh. Các đồ uống ngoài danh sách trên có thể được cung cấp tùy tình hình sau khi hỏi nhân viên.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '긱스타 PC방' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1. 세트 A, B, C를 성인에서 청소년 가격으로 변경  
2. 온종일 요금제(음료 미포함) 18,000원', 'Set A, B, and C available at youth rates. All-day pass excluding drinks: 18,000 KRW.', 'Set A・B・Cは青少年料金で利用可能。ドリンクなしの終日料金は18,000ウォン。', 'Set A, B, C được áp dụng giá thanh thiếu niên. Vé cả ngày không bao gồm đồ uống: 18.000 KRW.', '2026-02-25', '2026-12-18', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '놀숲' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '현금 또는 계좌이체로 10,000원 충전 시 10시간 + 음료 1잔 제공', 'Pay 10,000 KRW by cash or bank transfer to receive 10 hours plus 1 free drink.', '現金または口座振込で10,000ウォンをチャージすると、10時間＋ドリンク1杯を提供。', 'Nạp 10.000 KRW bằng tiền mặt hoặc chuyển khoản được tặng 10 giờ sử dụng và 1 đồ uống.', '2026-03-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '에이플 PC방' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '퇴실 시 학생증 혹은 유세인트 인증 시 15% 할인', '15% off with student ID or u-SAINT verification upon leaving.', '退店時に学生証またはu-SAINT認証で15%割引。', 'Giảm 15% khi xuất trình thẻ sinh viên hoặc xác minh u-SAINT lúc rời cửa hàng.', '2026-02-25', '2027-02-28', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '히어로 보드게임 카페' AND c.name_ko = '경영대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '15시 이후 방문 시 음료수 1개 제공, 리뷰 작성 시 비빔면 1개 추가 제공', 'Get 1 free drink when visiting after 3 PM. Get 1 additional serving of bibim noodles when leaving a review.', '15時以降の来店でドリンク1杯を無料提供。レビュー投稿でビビン麺1皿を追加提供。', 'Đến sau 3 giờ chiều được tặng 1 đồ uống. Khi viết đánh giá, được tặng thêm 1 phần mì trộn bibim.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도동 솥뚜껑' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '부리또 주문 시, 토핑 무료 추가 (베이컨 제외)', 'Get a free extra topping with any burrito order. Bacon excluded.', 'ブリトー注文時、トッピングを1つ無料追加。ベーコンは対象外。', 'Khi gọi burrito, được thêm miễn phí 1 topping. Không áp dụng cho thịt xông khói.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '밀플랜비' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이하 주문 시 음료수 1개, 3~4인 주문 시 음료수 2개 제공', 'For groups of 1–2, get 1 free drink. For groups of 3–4, get 2 free drinks.', '1〜2人でドリンク1杯、3〜4人でドリンク2杯を無料提供。', 'Nhóm 1–2 người được tặng 1 đồ uống. Nhóm 3–4 người được tặng 2 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '면식당' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '닭갈비 2인 이상 주문 시 치즈사리 제공', 'Get a free cheese add-on when ordering dakgalbi for 2 or more people.', 'タッカルビを2人分以上注文すると、チーズを無料追加。', 'Khi gọi dakgalbi cho từ 2 người trở lên, được thêm phô mai miễn phí.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '코웍 비스트로' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 주문 시 500ml 음료수 1개 제공', 'Get 1 free 500ml soft drink when ordering for 4 or more people.', '4人以上で注文すると、500mlのドリンク1本を無料提供。', 'Khi gọi món cho nhóm từ 4 người trở lên, được tặng 1 chai nước ngọt 500ml.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '맛닭꼬' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '오후 2시 30분~5시 30분 방문 시 토핑 무료 추가 (베이컨 제외)', 'Get a free extra topping when visiting between 2:30 PM and 5:30 PM. Bacon excluded.', '14時30分〜17時30分の来店で、トッピングを1つ無料追加。ベーコンは対象外。', 'Đến từ 2 giờ 30 đến 5 giờ 30 chiều được thêm miễn phí 1 topping. Không áp dụng cho thịt xông khói.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '부리또집' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '테이블당 음료수 1개 제공', '1 free drink per table.', '1テーブルにつきドリンク1杯を無料提供。', 'Mỗi bàn được tặng 1 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '리얼후라이' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4만원 이상 주문 시 감자튀김 제공', 'Get free French fries with orders of 40,000 KRW or more.', '40,000ウォン以上の注文でフライドポテトを無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng khoai tây chiên.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '사랑과 평화' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3만원 이상 주문 시 빙홍차 또는 차파이, 유토우 3p 제공', 'For orders of 30,000 KRW or more, get one Bing Hong Cha or Cha Pai drink, plus 3 pieces of youtiao.', '30,000ウォン以上の注文で、ビンホンチャまたはチャパイのうち1本と、油条3本を提供。', 'Đơn từ 30.000 KRW trở lên được tặng 1 chai Bing Hong Cha hoặc Cha Pai, kèm 3 miếng quẩy youtiao.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '샹츠마라' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '홀 식사 및 포장 시, 더블치즈버거 세트 9,900원
단품 포장 주문 시, 음료컵 무료 제공', 'For dine-in and takeout, the Double Cheeseburger Set is available for 9,900 KRW. Get a free drink cup with an à la carte takeout order.', '店内飲食・テイクアウトで、ダブルチーズバーガーセットを9,900ウォンで提供。単品をテイクアウト注文すると、ドリンクカップを無料提供。', 'Khi ăn tại quán hoặc mua mang đi, set Double Cheeseburger có giá 9.900 KRW. Khi mua lẻ mang đi, được tặng 1 cốc đồ uống miễn phí.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '크라이치즈버거' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '컵밥 2개 이상 주문 시 토핑 무료 제공 (볶음김치 or 계란)', 'Get one free topping when ordering 2 or more cupbap dishes. Choose either stir-fried kimchi or egg.', 'カップ飯を2個以上注文すると、炒めキムチまたは卵のうちトッピング1つを無料提供。', 'Khi gọi từ 2 phần cơm cốc trở lên, được chọn miễn phí 1 topping: kimchi xào hoặc trứng.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '아리랑컵밥' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4만원 이상 주문 시 소주 1병 제공', 'Get 1 free bottle of soju with orders of 40,000 KRW or more.', '40,000ウォン以上の注文で焼酎1本を無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng 1 chai soju.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '숯가마 바베큐 치킨' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '7만원 이상 현금 결제 시 김치볶음밥 또는 계란말이 중 1개 제공', 'For cash payments of 70,000 KRW or more, choose one: Kimchi Fried Rice or Rolled Omelet.', '70,000ウォン以上の現金決済で、キムチチャーハンまたは卵焼きのうち1つを提供。', 'Thanh toán tiền mặt từ 70.000 KRW trở lên được chọn 1 món: cơm chiên kimchi hoặc trứng cuộn.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블루힐' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4만원 이상 주문 시 음료수 1개 제공', 'Get 1 free drink with orders of 40,000 KRW or more.', '40,000ウォン以上の注文でドリンク1杯を無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng 1 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '파동추야' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '안주 주문 시 음료수 1개 제공', 'Get 1 free soft drink when ordering any food item.', 'おつまみを注文すると、ドリンク1杯を無料提供。', 'Khi gọi món nhắm, được tặng 1 đồ uống.', '2026-09-01', '2026-06-22', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '가치' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '음료 구매 시 10% 할인', '10% off all drink purchases.', 'ドリンク購入時、10%割引。', 'Giảm 10% khi mua đồ uống.', '2026-09-01', '2026-06-22', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = 'eea cafe' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '학생증 제시 시 15% 할인', '15% off with student ID.', '学生証の提示で15%割引。', 'Giảm 15% khi xuất trình thẻ sinh viên.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '히어로 보드게임 카페' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1만원 이상 계좌이체 또는 현금 결제 시 10시간 및 서비스 음료 제공', 'Pay 10,000 KRW or more by cash or bank transfer to receive 10 hours of PC use and 1 complimentary drink.', '10,000ウォン以上を現金または口座振込で支払うと、PC利用10時間とドリンク1杯を提供。', 'Thanh toán từ 10.000 KRW trở lên bằng tiền mặt hoặc chuyển khoản được sử dụng PC trong 10 giờ và nhận 1 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '에이플 PC방' AND c.name_ko = '인문대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '오후 2:30~5:30 사이 방문 시 추가 토핑 4종 (콩, 할라피뇨, 스위트콘, 더블치즈) 중 택 1 추가', 'Choose 1 free topping (beans, jalapeños, sweet corn, or double cheese) for visits between 2:30 PM and 5:30 PM.', '2:30 PM〜5:30 PMの来店で、豆・ハラペーニョ・スイートコーン・ダブルチーズのうちトッピング1つを無料追加。', 'Đến từ 2:30 PM đến 5:30 PM được chọn thêm miễn phí 1 topping: đậu, ớt jalapeño, bắp ngọt hoặc phô mai đôi.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '부리또집' AND c.name_ko = '법과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이상 방문 시, 치즈사리 추가 제공', 'Free cheese add-on for groups of 2 or more.', '2人以上の来店でチーズ追加を無料提供。', 'Nhóm từ 2 người trở lên được tặng phần phô mai thêm.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '코웍 비스트로' AND c.name_ko = '법과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '주문금액 30,000원 당 펩시 제로 슈거 라임 1.25L 제공 (포장 및 매장식사만 적용)', 'Free 1.25L Pepsi Zero Sugar Lime for every 30,000 KRW spent. Dine-in and takeout only.', '30,000ウォンごとの利用で1.25L ペプシゼロシュガーライムを無料提供。
 店内飲食・テイクアウトのみ対象。', 'Tặng 1.25L Pepsi Zero Sugar Lime cho mỗi 30.000 KRW chi tiêu.
 Chỉ áp dụng cho ăn tại chỗ và mang đi.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '피자스쿨' AND c.name_ko = '법과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 방문 시 군만두 4개 서비스 or 탄산 음료 1캔 (테이블 당 1개)', 'For groups of 4, choose one: 4 complimentary fried dumplings or 1 canned soft drink. One benefit per table.', '4人で来店すると、焼き餃子4個または缶入り炭酸飲料1本のうち1つを無料提供。1テーブルにつき1つまで。', 'Nhóm 4 người được chọn 1 trong 2: 4 bánh mandu chiên hoặc 1 lon nước ngọt. Mỗi bàn chỉ được nhận 1 phần ưu đãi.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '취향' AND c.name_ko = '법과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이하 음료수 1개 제공 / 3인 이상 4인 이하 음료수 2개 제공', 'For groups of 1–2, get 1 free drink. For groups of 3–4, get 2 free drinks.', '1〜2人でドリンク1杯を無料提供。
 3〜4人で2 ドリンクを無料提供。', 'Nhóm 1–2 người được tặng 1 đồ uống.
 Nhóm 3–4 người được tặng 2 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '면식당' AND c.name_ko = '법과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 방문 시 음료 1병 제공 / 5만원 이상 결제 시 소주 1병 음료 1병 제공', 'Free bottled soft drink for groups of 4 or more, or 1 free bottle of soju plus 1 bottled soft drink for payments of 50,000 KRW or more.', '4人以上の来店でボトルドリンク1本、または50,000ウォン以上の決済で焼酎1本＋ボトルドリンク1本を無料提供。', 'Nhóm từ 4 người trở lên được tặng 1 chai nước ngọt; hoặc thanh toán từ 50.000 KRW trở lên được tặng 1 chai soju + 1 chai nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '신의주 찹쌀순대' AND c.name_ko = '법과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1. 2만원 이상 주문 시 음료수 2개 제공
2. 3만원 이상 주문 시 단품을 세트로 변경 (주문 건 모두)', 'For orders of 20,000 KRW or more, get 2 free drinks. For orders of 30,000 KRW or more, upgrade all single items to set menus.', '20,000ウォン以上の注文でドリンク2杯を無料提供。
 30,000ウォン以上の注文で単品をすべてセットメニューに変更。', 'Đơn từ 20.000 KRW trở lên được tặng 2 đồ uống.
 Đơn từ 30.000 KRW trở lên được nâng tất cả món lẻ thành set menu.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '지지고' AND c.name_ko = '법과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '50,000원 이상 주문 시 모둠 소시지 제공', 'Get a free assorted sausage platter with orders of 50,000 KRW or more.', '50,000ウォン以上の注文で、ソーセージ盛り合わせを無料提供。', 'Đơn từ 50.000 KRW trở lên được tặng một phần xúc xích thập cẩm.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '역전할머니맥주' AND c.name_ko = '법과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '빅 사이즈 아메리카노 두 잔 구매 시 한 잔에 샷 추가 / 빅 사이즈 아메리카노 + 아이스티 구매 시 한 잔에 샷 추가', 'Free extra shot in one drink when ordering either 2 Big Size Americanos or 1 Big Size Americano plus 1 Iced Tea.', 'ビッグサイズアメリカーノ2杯、またはビッグサイズアメリカーノ1杯＋アイスティー1杯注文時、1杯にショット1回無料追加。', 'Gọi 2 Americano cỡ lớn hoặc 1 Americano cỡ lớn + 1 trà đá được thêm 1 shot miễn phí vào 1 ly.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '카페 봄봄' AND c.name_ko = '법과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5,000원 충전 시 탄산음료 제공
10,000원 이상 충전 시 카페음료 제공', 'Free soda when adding 5,000 KRW of credit
Free café drink when adding 10,000 KRW or more of credit', '5,000ウォンチャージで炭酸飲料を無料提供。
 10,000ウォン以上のチャージでカフェドリンクを無料提供。', 'Nạp 5.000 KRW được tặng soda.
 Nạp từ 10.000 KRW trở lên được tặng đồ uống cà phê.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '긱스타 PC방' AND c.name_ko = '법과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 방문 시 음료 1개 제공
리뷰이벤트 작성 시 비빔면 1개 제공', 'Get 1 free drink for groups of 4 or more.
Get 1 free serving of bibim noodles when you leave a review.', '4人以上の来店でドリンク1杯を無料提供。
レビューを投稿すると、ビビン麺1皿を無料提供。', 'Nhóm từ 4 người trở lên được tặng 1 đồ uống.
Khi viết đánh giá, được tặng 1 phần mì trộn bibim.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도동 솥뚜껑' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3만원 이상 주문 시 음료 2개/소주/맥주/유토우 제공
*합칠 수 있는 영수증 최대 3개', 'For orders of 30,000 KRW or more, choose one of the following: 2 soft drinks, soju, beer, or youtiao.
*Up to 3 receipts may be combined.', '30,000ウォン以上の注文で、ドリンク2本、焼酎、ビール、または油条（ヨウティアオ）の中から1つを提供。
*合算できるレシートは最大3枚まで。', 'Đơn từ 30.000 KRW trở lên được chọn 1 trong các ưu đãi sau: 2 đồ uống, soju, bia hoặc quẩy youtiao. 
*Có thể gộp tối đa 3 hóa đơn.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '샹츠마라' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '부리또 주문 시 토핑 추가(베이컨 제외)', 'Get 1 free extra topping with any burrito order. Bacon excluded.', 'ブリトーを注文すると、トッピングを1つ無料追加。ベーコンは対象外。', 'Khi gọi burrito, được thêm miễn phí 1 topping. Không áp dụng cho thịt xông khói.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '밀플랜비' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '테이블당 메뉴 2개 이상 주문 시 음료 서비스(12-14시, 17-19시 제외)', 'Get 1 free drink per table when ordering at least 2 menu items.
*Not available from 12 PM to 2 PM or from 5 PM to 7 PM.', '1テーブルにつき、メニューを2品以上注文するとドリンク1杯を無料提供。
*12時〜14時および17時〜19時は対象外。', 'Mỗi bàn gọi từ 2 món trở lên được tặng 1 đồ uống.
*Không áp dụng từ 12:00 đến 14:00 và từ 17:00 đến 19:00.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '고추동 제면소' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이하 방문 시 음료 1개 제공
3인 이상 4인 이하 방문 시 음료 2개 제공', 'Groups of up to 2 get 1 free drink.
Groups of 3–4 get 2 free drinks.', '1〜2人の来店でドリンク1杯、3〜4人の来店でドリンク2杯を無料提供。', 'Nhóm 1–2 người được tặng 1 đồ uống; nhóm 3–4 người được tặng 2 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '면식당' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이상 주문 시 음료 1개 제공
4인 주문 시 음료 2개 제공', 'Groups of 2 or more get 1 free drink.
Groups of 4 get 2 free drinks.', '2人以上の注文でドリンク1杯、4人での注文でドリンク2杯を無料提供。', 'Đơn dành cho từ 2 người trở lên được tặng 1 đồ uống; đơn dành cho 4 người được tặng 2 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '핵밥' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만원 이상 주문 시 갈릭빠다포테이토 또는 양념감자 제공', 'For orders of 50,000 KRW or more, choose one: Garlic Butter Potatoes or Seasoned Fries.', '50,000ウォン以上の注文で、ガーリックバターポテトまたは味付けフライドポテトのうち1つを無料提供。', 'Đơn từ 50.000 KRW trở lên được chọn miễn phí 1 trong 2 món: khoai tây bơ tỏi hoặc khoai tây chiên tẩm gia vị.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '역전할머니맥주' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '메뉴 2개 이상 주문 후 현금, 계좌이체 또는 카카오페이 결제 시 5% 할인', 'Get 5% off when ordering at least 2 menu items and paying by cash, bank transfer, or Kakao Pay.', 'メニューを2品以上注文し、現金・口座振込・Kakao Payのいずれかで支払うと5%割引。', 'Gọi từ 2 món trở lên và thanh toán bằng tiền mặt, chuyển khoản hoặc Kakao Pay được giảm 5%.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '인쌩맥주' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3만원 이상 주문 시 음료 제공', 'Get 1 free drink with orders of 30,000 KRW or more.', '30,000ウォン以上の注文でドリンク1杯を無料提供。', 'Đơn từ 30.000 KRW trở lên được tặng 1 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '파동추야' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '7만원 이상 주문 시 김치볶음밥 또는 계란말이 제공
*현금, 계좌이체 결제 시 적용', 'For orders of 70,000 KRW or more, choose one: Kimchi Fried Rice or Rolled Omelet.
*Valid for cash or bank transfer payments only.', '70,000ウォン以上の注文で、キムチチャーハンまたは卵焼きのうち1品を無料提供。
*現金または口座振込での支払いに限る。', 'Đơn từ 70.000 KRW trở lên được chọn miễn phí 1 món: cơm chiên kimchi hoặc trứng cuộn. 
*Chỉ áp dụng khi thanh toán bằng tiền mặt hoặc chuyển khoản.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블루힐' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '테이블 당 4만원 이상 주문 시 감자튀김 제공', 'Get free French fries with orders of 40,000 KRW or more per table.', '1テーブルにつき40,000ウォン以上の注文で、フライドポテトを無料提供。', 'Mỗi bàn gọi từ 40.000 KRW trở lên được tặng khoai tây chiên.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '사랑과 평화' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2만원 이상 주문 시 라면 1개 제공', 'Get 1 free ramyeon with orders of 20,000 KRW or more.', '20,000ウォン以上の注文でラーメン1杯を無料提供。', 'Đơn từ 20.000 KRW trở lên được tặng 1 phần mì ramyeon.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '씨밤' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5% 할인
*현금, 계좌이체 결제 시 적용', 'Get 5% off.
*Valid for cash or bank transfer payments only.', '5%割引。
*現金または口座振込での支払いに限る。', 'Giảm 5%.
*Chỉ áp dụng khi thanh toán bằng tiền mặt hoặc chuyển khoản.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도로 3가' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '모든 음료 주문 시, 타피오카펄 무료 추가 (폼, 다른 토핑류 X)
점보 사이즈는 펄 라지 양으로 제공
*중복 혜택 적용 불가 (공차데이, 통신사 할인폭이 큰 이벤트의 경우)
*동행 적용 가능 (학생증 증명 필수)', 'Get free tapioca pearls with any drink order. Foam and other toppings are excluded.
For jumbo drinks, the amount of pearls provided will be the same as for a large size.
*Cannot be combined with other promotions, including Gong Cha Day or mobile carrier promotions offering greater discounts.
*The benefit also applies to accompanying guests. Student ID verification is required.', 'すべてのドリンク注文時、タピオカパールを無料追加。フォームやその他のトッピングは対象外。
ジャンボサイズには、ラージサイズ相当量のパールを提供。
*ゴンチャデーや通信会社の割引率が高いキャンペーンなど、他の特典との併用不可。
*同伴者にも適用可能。学生証の提示必須。', 'Khi gọi bất kỳ đồ uống nào, được thêm trân châu tapioca miễn phí. Không áp dụng cho lớp foam hoặc các loại topping khác.
Với size Jumbo, lượng trân châu được tặng tương đương size Large.
*Không áp dụng đồng thời với ưu đãi khác, bao gồm Gong Cha Day hoặc chương trình giảm giá cao của nhà mạng.
*Ưu đãi áp dụng cho cả người đi cùng. Bắt buộc xuất trình thẻ sinh viên.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '공차' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2잔(빅 아메리카노, 아이스티 각각 1잔) 주문 시 샷 추가 1번 제공', 'Get 1 free extra espresso shot when ordering 2 drinks: 1 Big Americano and 1 iced tea.', 'ビッグサイズのアメリカーノ1杯とアイスティー1杯、計2杯の注文で、エスプレッソショット1杯を無料追加。', 'Khi gọi 2 đồ uống gồm 1 Americano cỡ lớn và 1 trà đá, được thêm miễn phí 1 shot espresso.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '카페 봄봄' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '9시간 충전 시 10시간 + 음료 제공
*현금, 계좌이체 결제 시 적용', 'Purchase 9 hours of PC time and receive 10 hours plus 1 free drink.
*Valid for cash or bank transfer payments only.', 'PC利用時間を9時間分チャージすると、10時間利用でき、ドリンク1杯を無料提供。
*現金または口座振込での支払いに限る。', 'Nạp gói 9 giờ, được nâng lên 10 giờ sử dụng PC và tặng 1 đồ uống. 
*Chỉ áp dụng khi thanh toán bằng tiền mặt hoặc chuyển khoản.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '에이플 PC방' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '세트 A, B, C 요금제를 청소년으로 변경', 'Sets A, B, and C are available at the youth rate.', 'セットA・B・Cを青少年料金で利用可能。', 'Các gói A, B và C được áp dụng mức giá dành cho thanh thiếu niên.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '놀숲' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5,000원 충전 시 탄산 제공
10,000원 이상 충전 시 카페 음료 제공', 'Get 1 free soda when adding 5,000 KRW of credit. 
Get 1 free café drink when adding at least 10,000 KRW of credit.', '5,000ウォンをチャージすると炭酸飲料1杯、10,000ウォン以上をチャージするとカフェドリンク1杯を無料提供。', 'Nạp 5.000 KRW được tặng 1 đồ uống có ga; nạp từ 10.000 KRW trở lên được tặng 1 đồ uống café.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '긱스타 PC방' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '음료 2잔 주문 시 1샷 추가
자유전공학부생 인증 시 1샷 추가
*매주 월요일만, 학생증 제시', 'Get 1 extra shot when ordering 2 drinks.
Verified students of the School of Liberal Studies receive 1 additional shot.
*Available on Mondays only. Student ID required.', 'ドリンク2杯の注文で、エスプレッソショット1杯を無料追加。
自由専攻学部（School of Liberal Studies）の学生であることを確認できた場合、さらに1ショットを無料追加。
*毎週月曜日のみ。学生証の提示必須。', 'Khi gọi 2 đồ uống, được thêm miễn phí 1 shot espresso.
Sinh viên thuộc School of Liberal Studies sau khi xác minh được thêm miễn phí 1 shot nữa.
*Chỉ áp dụng vào thứ Hai hằng tuần. Bắt buộc xuất trình thẻ sinh viên.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '빽다방' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '게임 이용료 15% 할인', 'Get 15% off game fees.', 'ゲーム利用料15%割引。', 'Giảm 15% phí chơi game.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '히어로 보드게임 카페' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '10,000원 충전 시 5곡 추가
*현금, 계좌이체 결제 시 적용', 'Get 5 extra songs when adding 10,000 KRW of credit.
*Valid for cash or bank transfer payments only.', '10,000ウォンをチャージすると、5曲追加。
*現金または口座振込での支払いに限る。', 'Nạp 10.000 KRW được tặng thêm 5 bài hát.
*Chỉ áp dụng khi thanh toán bằng tiền mặt hoặc chuyển khoản.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블라블라 코인노래방' AND c.name_ko = '자유전공학부';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '오후 2:30~오후 5:30 구매 시 토핑 추가 1회 무료', 'Get one free topping add-on for purchases between 2:30 PM and 5:30 PM.', '14時30分〜17時30分に購入すると、トッピングを1つ無料追加。', 'Mua hàng từ 2 giờ 30 đến 5 giờ 30 chiều được thêm miễn phí 1 topping.', '2026-09-01', '2026-12-18', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '부리또집' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '30000원 이상 구매시(단 영수증 3개 미만) 유토루 3 pcs, 차파이, 삐홍차 중 택 1
*당일 영수증 한정', 'For purchases of 30,000 KRW or more, choose one: 3 pieces of youtiao, Cha Pai, or Bing Hong Cha.
*Fewer than 3 receipts may be combined. Same-day receipts only.', '30,000ウォン以上の購入で、油条（ヨウティアオ）3本、チャパイ、またはビンホンチャのうち1つを提供。
*合算できるレシートは3枚未満。当日発行のレシートのみ有効。', 'Mua từ 30.000 KRW trở lên được chọn 1 trong 3 ưu đãi: 3 miếng quẩy youtiao, Cha Pai hoặc Bing Hong Cha.
*Có thể gộp tối đa 2 hóa đơn. Chỉ áp dụng cho hóa đơn trong ngày.', '2026-09-01', '2026-12-11', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '샹츠마라' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '메인 토핑 주문시 닭가슴살 또는 훈제오리 토핑 30g 추가', 'Get an extra 30g of chicken breast or smoked duck topping with a main topping order.', 'メイントッピング注文時、鶏むね肉または燻製鴨のトッピングを30g追加提供。', 'Khi gọi topping chính, được thêm 30g topping ức gà hoặc vịt hun khói.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '포케올데이' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 방문 음료 1개 무료, 3~4인 방문 시 음료 2개 무료', 'Groups of 2 get 1 free drink.
Groups of 3–4 get 2 free drinks.', '2人での来店でドリンク1杯、3〜4人での来店でドリンク2杯を無料提供。', 'Nhóm 2 người được tặng 1 đồ uống; nhóm 3–4 người được tặng 2 đồ uống.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '면식당' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 주문 시 한 테이블, 인당 군만두 1개 또는 500ml 음료 제공', 'For tables of 4 or more, choose either 1 fried dumpling per person or 1 500ml drink.', '4人以上のテーブル注文で、1人につき焼き餃子1個、または500mlドリンク1本のうちいずれかを提供。', 'Bàn từ 4 người trở lên được chọn 1 trong 2 ưu đãi: mỗi người 1 bánh mandu chiên hoặc 1 chai đồ uống 500ml.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '취향' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '부리또 주문 시 토핑 추가 무료 (베이컨 제외)', 'Get 1 free extra topping with any burrito order. Bacon excluded.', 'ブリトーを注文すると、トッピングを1つ無料追加。ベーコンは対象外。', 'Khi gọi burrito, được thêm miễn phí 1 topping. Không áp dụng cho thịt xông khói.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '밀플랜비' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1. 같은 과 4인 방문 시 음료수 한 캔 무료
2. 5만원 이상 구매 시 소주 한 병 무료 (1, 2번 중복 X)', '1. Groups of 4 students from the same department get 1 free canned drink.
2. Get 1 free bottle of soju with purchases of 50,000 KRW or more.
*Benefits 1 and 2 cannot be combined.', '1. 同じ学科の学生4人で来店すると、缶ドリンク1本を無料提供。
2. 50,000ウォン以上の購入で焼酎1本を無料提供。
*1と2の特典は併用不可。', '1. Nhóm 4 sinh viên cùng khoa được tặng 1 lon nước ngọt.
2. Mua từ 50.000 KRW trở lên được tặng 1 chai soju.
*Không được áp dụng đồng thời ưu đãi 1 và 2.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '신의주 찹쌀순대' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만원 이상 구매 시, 갈릭빠따포테이토(미니) or 참이슬 한 병 중 택1', 'For purchases of 50,000 KRW or more, choose one: Mini Garlic Butter Potatoes or 1 bottle of Chamisul.', '50,000ウォン以上の購入で、ミニサイズのガーリックバターポテトまたはチャミスル1本のうち1つを無料提供。', 'Mua từ 50.000 KRW trở lên được chọn miễn phí 1 trong 2: khoai tây bơ tỏi cỡ nhỏ hoặc 1 chai Chamisul.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '역전할머니맥주' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '18시 이전 방문 시 칵테일 소주 500ml 제공, 7만원 이상 현금 결제 시 김치볶음밥 하나 제공', 'Visit before 6 PM to receive 500ml of cocktail soju.
For cash payments of 70,000 KRW or more, get 1 free Kimchi Fried Rice.', '18時前の来店でカクテル焼酎500mlを無料提供。
70,000ウォン以上を現金で支払うと、キムチチャーハン1皿を無料提供。', 'Đến trước 6 giờ tối được tặng 500ml soju cocktail.
Thanh toán tiền mặt từ 70.000 KRW trở lên được tặng 1 phần cơm chiên kimchi.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블루힐' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만원 이상 구매시 소주 한 병 무료 *일행당 1병', 'Free 1 bottle of soju for purchases of 50,000 KRW or more (1 bottle per group).', '50,000ウォン以上の購入で焼酎1本を無料提供。1グループにつき1本まで。', 'Mua từ 50.000 KRW trở lên được tặng 1 chai soju. Mỗi nhóm được nhận tối đa 1 chai.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '젠사이야' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '목, 금 중에 하루 2잔 이상 주문 시 한 잔 샷 추가', 'On one designated day, either Thursday or Friday, get 1 free extra shot in one drink when ordering 2 or more drinks.', '木曜日または金曜日のうち指定された1日に、ドリンクを2杯以上注文すると、うち1杯にショット1回を無料追加。', 'Vào một ngày được chỉ định, thứ Năm hoặc thứ Sáu, khi gọi từ 2 đồ uống trở lên, được thêm miễn phí 1 shot vào 1 ly.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '빽다방' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '숭실대 요금표: SET A, B, C 요금제 성인 -> 청소년 변경', 'Soongsil University pricing: Set A, B, and C available at youth rates.', '崇実大学料金：セットA・B・Cを青少年料金で利用可能。', 'Bảng giá dành cho Đại học Soongsil: các gói A, B và C được áp dụng mức giá dành cho thanh thiếu niên', '2026-09-01', '2026-12-14', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '놀숲' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1. 게임비 15% 할인
2. 계좌이체 시 빙수 10% 할인
*카운터 직접 결제 (키오스크 또는 태블릿 X)', '1. Get 15% off game fees.
2. Get 10% off shaved ice when paying by bank transfer.
*Payment must be made directly at the counter. Kiosk and tablet payments are not eligible.', '1. ゲーム利用料15%割引。
2. 口座振込で支払うと、かき氷10%割引。
*カウンターでの直接決済に限る。キオスクおよびタブレット決済は対象外。', '1. Giảm 15% phí chơi game.
2. Giảm 10% món bingsu khi thanh toán bằng chuyển khoản.
*Phải thanh toán trực tiếp tại quầy. Không áp dụng khi thanh toán bằng kiosk hoặc máy tính bảng.', '2026-09-01', '2026-12-11', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '히어로 보드게임 카페' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '아메리카노 빅사이즈 2잔 주문 시 또는 아메리카노 + 아이스티 주문 시, 둘 중 한 잔에 샷 추가', 'Get 1 free extra shot in one of the two drinks when ordering either 2 Big Americanos or 1 Americano and 1 iced tea.', 'ビッグサイズのアメリカーノ2杯、またはアメリカーノ1杯とアイスティー1杯を注文すると、2杯のうち1杯にショット1回を無料追加。', 'Khi gọi 2 Americano cỡ lớn hoặc 1 Americano và 1 trà đá, được thêm miễn phí 1 shot vào 1 trong 2 ly.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '카페 봄봄' AND c.name_ko = '자연과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1인 1메뉴 주문 시 2인 이상 음료수 1캔 / 4인 이상 음료수 2캔 제공', 'When each person orders 1 menu item: 1 free canned soft drink for groups of 2 or more, or 2 free canned soft drinks for groups of 4 or more.', '1人1メニュー注文時、2人以上は缶飲料1本、4人以上は缶飲料2本を提供。', 'Khi mỗi người gọi 1 món: nhóm từ 2 người được tặng 1 lon nước ngọt, nhóm từ 4 người được tặng 2 lon.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '멘동' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3만원 이상 주문 시 유토우 또는 음료(차파이/빙홍차) 중 1개 제공 (영수증 최대 3개까지만 취합 가능)', 'For orders of 30,000 KRW or more, choose one: Youtiao, Cha Pai, or Bing Hong Cha. Up to 3 receipts may be combined.', '30,000ウォン以上の注文で、油条・茶π・冰紅茶のうち1つを選択。レシートは最大3枚まで合算可能。
 レシートは最大3枚まで合算可能。', 'Đơn từ 30.000 KRW trở lên được chọn 1 trong các món: quẩy Youtiao, Cha Pai hoặc Bing Hong Cha. Có thể gộp tối đa 3 hóa đơn.
 Có thể gộp tối đa 3 hóa đơn.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '샹츠마라' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '부리또 주문 시 토핑 1개 무료(베이컨 제외)', 'Free topping add-on for burrito orders, excluding bacon.', 'ブリトー注文時、ベーコンを除くトッピング1つ無料。', 'Gọi burrito được thêm 1 topping miễn phí, trừ bacon.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '밀플랜비' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '두 마리 이상 주문 시 치즈스틱 4피스 제공 (포장, 매장 둘 다 가능)', 'Order 2 or more whole chickens and receive 4 free cheese sticks. Available for both dine-in and takeout.', 'チキンを2羽以上注文すると、チーズスティック4本を無料提供。店内飲食・テイクアウトのどちらも対象。', 'Gọi từ 2 con gà trở lên được tặng 4 thanh phô mai. Áp dụng cho cả ăn tại quán và mang đi.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '미쳐버린 파닭' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1인당 음료수 1캔 또는 해시브라운 2피스 제공', 'Each person may choose one complimentary option: 1 canned soft drink or 2 hash browns.', '1人につき、缶ドリンク1本またはハッシュブラウン2個のうち1つを無料提供。', 'Mỗi người được chọn miễn phí 1 lon nước ngọt hoặc 2 miếng hash brown.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '미태리' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이하 방문 시 음료수 1캔 / 3~4인 방문 시 음료수 2캔 제공', 'For groups of 1–2, get 1 canned soft drink. For groups of 3–4, get 2 canned soft drinks.', '1〜2人で缶ドリンク1本を無料提供。
 3〜4人で2 缶ドリンクを無料提供。', 'Nhóm 1–2 người được tặng 1 lon nước ngọt.
 Nhóm 3–4 người được tặng 2 nước ngọt đóng lon.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '면식당' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 주문 시 음료수 1캔 제공 또는 4인 이상 방문 후 5만원 이상 주문 시 소주 한 병 제공', 'For groups of 4 or more, get 1 canned soft drink. Or, for groups of 4 or more spending 50,000 KRW or more, get 1 free bottle of soju.', '4人以上で缶ドリンク1本を無料提供。
 また、4人以上で50,000ウォン以上利用時、焼酎1本を無料提供。', 'Nhóm từ 4 người trở lên được tặng 1 lon nước ngọt.
 Hoặc nhóm từ 4 người trở lên chi từ 50.000 KRW trở lên được tặng 1 chai soju.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '신의주 찹쌀순대' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '메인 토핑 주문 시(사이드 메뉴 제외) 닭가슴살 또는 훈제오리 토핑 30g 추가 제공', 'Add 30g of chicken breast or smoked duck topping when ordering a main topping, excluding side menus.', 'メイントッピング注文時（サイドメニュー除く）、鶏むね肉または燻製鴨トッピング30gを追加提供。', 'Khi gọi topping chính, được thêm 30g topping ức gà hoặc vịt hun khói, không áp dụng cho món phụ.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '포케올데이' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 방문 시 돌솥 국물 떡볶이 1인분 또는 콜라 500ml 제공', 'For groups of 4 or more, choose one: 1 serving of hot-stone soupy tteokbokki or 500ml cola.', '4人以上の来店で、石鍋スープトッポッキ1人前またはコーラ500mlのうち1つを選択。', 'Nhóm từ 4 người trở lên được chọn 1 món: 1 phần tteokbokki nước trong nồi đá hoặc cola 500ml.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '파라다이스' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3인 이상 주문 시 음료캔 1개 제공 / 6인 이상 주문 시 음료캔 2개 제공 / 9인 이상 주문 시 음료캔 3개 제공', 'Free canned drinks based on order size: 1 can for orders of 3 or more people, 2 cans for 6 or more, and 3 cans for 9 or more.', '人数に応じて缶飲料を無料提供：3人は1缶、6人は2缶、9人は3缶。', 'Tặng nước lon theo số người/khẩu phần: 1 lon cho 3 người, 2 lon cho 6 người, 3 lon cho 9 người.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '삼봉버거' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3인 이상 주문 시 음료캔 1개 제공 / 6인 이상 주문 시 음료캔 2개 제공 / 9인 이상 주문 시 음료캔 3개 제공', 'Free canned drinks based on order size: 1 can for orders of 3 or more people, 2 cans for 6 or more, and 3 cans for 9 or more.', '人数に応じて缶飲料を無料提供：3人は1缶、6人は2缶、9人は3缶。', 'Tặng nước lon theo số người/khẩu phần: 1 lon cho 3 người, 2 lon cho 6 người, 3 lon cho 9 người.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '핵밥' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이상 방문 시 콜라 또는 사이다 1캔 제공', 'For groups of 2 or more, get 1 free can of cola or lemon-lime soda.', '2人以上の来店で、コーラまたはサイダー1缶を無料提供。', 'Nhóm từ 2 người trở lên được tặng 1 lon cola hoặc soda chanh.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '슬로우캘리' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '세트 메뉴 2개 이상 주문 시 음료수 1캔 제공
단품 메뉴 3개 이상 주문 시 음료수 1캔 제공', 'Order 2 or more set menus and receive 1 free canned soft drink.
Order 3 or more à la carte items and receive 1 free canned soft drink.', 'セットメニューを2つ以上注文すると、缶ドリンク1本を無料提供。
単品メニューを3品以上注文すると、缶ドリンク1本を無料提供。', 'Gọi từ 2 set menu trở lên được tặng 1 lon nước ngọt.
Gọi từ 3 món lẻ trở lên được tặng 1 lon nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '불백스테이션' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2만원 이상 주문 시 음료 2개 제공
3만원 이상 주문 시 단품을 세트로 변경', 'Orders of 20,000 KRW or more include 2 free drinks.
For orders of 30,000 KRW or more, à la carte items are upgraded to set menus at no extra charge.', '20,000ウォン以上の注文で、ドリンク2杯を無料提供。
30,000ウォン以上の注文で、単品メニューを追加料金なしでセットメニューに変更。', 'Đơn từ 20.000 KRW trở lên được tặng 2 đồ uống.
Đơn từ 30.000 KRW trở lên được nâng các món lẻ thành set menu mà không mất thêm phí.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '지지고' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2만원 이상 주문 시 음료수 1캔 제공', 'Get 1 free canned soft drink with orders of 20,000 KRW or more.', '20,000ウォン以上の注文で、缶ドリンク1本を無料提供。', 'Đơn từ 20.000 KRW trở lên được tặng 1 lon nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '왕돈까스왕냉면' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 방문 시 군만두 무료 제공
(단, 주문 시 제휴 요청해야 하며 메뉴 나온 후 요청 시 거절될 수 있음)
계좌이체 시 1,000원 할인', 'Groups of 4 or more receive free fried dumplings.
*The partnership benefit must be requested when ordering. Requests made after the food is served may be declined.
Get 1,000 KRW off when paying by bank transfer.', '4人以上の来店で、焼き餃子を無料提供。
*注文時に提携特典を申告してください。料理提供後の申告は断られる場合があります。
口座振込で支払うと、1,000ウォン割引。', 'Nhóm từ 4 người trở lên được tặng mandu chiên.
*Phải yêu cầu áp dụng ưu đãi liên kết khi gọi món. Yêu cầu sau khi món ăn đã được phục vụ có thể bị từ chối.
Giảm 1.000 KRW khi thanh toán bằng chuyển khoản.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '황궁쟁반짜장' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1인 1메뉴 주문 시 테이블당 음료 1캔
(점심 12시-2시, 저녁 5시-7시 제외)', 'Get 1 free canned soft drink per table when each person orders 1 menu item.
*Not available from 12 PM to 2 PM or from 5 PM to 7 PM.', '1人1品を注文すると、1テーブルにつき缶ドリンク1本を無料提供。
*12時〜14時および17時〜19時は対象外。', 'Khi mỗi người gọi 1 món, mỗi bàn được tặng 1 lon nước ngọt.
*Không áp dụng từ 12:00 đến 14:00 và từ 17:00 đến 19:00.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '고추동 제면소' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT 'AI대학 학생증 제시 시 음료수 제공
리뷰 작성 시 비빔면 제공', 'Present an AI College student ID to receive 1 free drink.
Leave a review to receive 1 free serving of bibim noodles.', 'AI大学の学生証を提示すると、ドリンク1杯を無料提供。
レビューを投稿すると、ビビン麺1皿を無料提供。', 'Xuất trình thẻ sinh viên College of AI để được tặng 1 đồ uống.
Viết đánh giá để được tặng 1 phần mì trộn bibim.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도동 솥뚜껑' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만원 이상 주문 시 수제소시지 또는 참이슬 1병 제공', 'For orders of 50,000 KRW or more, choose one complimentary item: Handmade Sausage or 1 bottle of Chamisul.', '50,000ウォン以上の注文で、手作りソーセージまたはチャミスル1本のうち1つを無料提供。', 'Đơn từ 50.000 KRW trở lên được chọn miễn phí 1 trong 2: xúc xích thủ công hoặc 1 chai Chamisul.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '역전할머니맥주' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4만원 이상 주문 시 음료수 1캔 제공', 'Free canned soft drink for orders of 40,000 KRW or more.', '40,000ウォン以上の注文で缶ドリンクを無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng nước ngọt đóng lon.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '파동추야' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '계좌이체 결제 시 5% 할인', '5% off when paying by bank transfer.', '口座振込決済時、5%割引。', 'Giảm 5% khi thanh toán bằng chuyển khoản.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '인쌩맥주' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 방문 시 음료수 1캔 제공 또는 7만원 이상 현금 결제 시 김치볶음밥 또는 계란말이 제공', 'For groups of 4, choose one: 1 canned soft drink, or Kimchi Fried Rice/Rolled Omelet for cash payments of 70,000 KRW or more.', '4人来店時は缶ドリンク1本、または70,000ウォン以上の現金決済時はキムチチャーハン/卵焼きのうち1つを選択。', 'Nhóm 4 người được chọn 1 lon nước ngọt; hoặc nếu thanh toán tiền mặt từ 70.000 KRW trở lên, được chọn cơm chiên kimchi hoặc trứng cuộn.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블루힐' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5,000원 충전 시 탄산음료 1개 제공 / 10,000원 충전 시 탄산음료, 아메리카노, 아이스티 중 1개 제공', 'Get a free soda when adding 5,000 KRW of credit. Choose one free drink when adding 10,000 KRW of credit: soda, Americano, or iced tea.', '5,000ウォンチャージで炭酸飲料1杯を無料提供。
 10,000ウォンチャージで炭酸飲料・アメリカーノ・アイスティーのうち1杯を無料提供。', 'Nạp 5.000 KRW được tặng 1 soda.
 Nạp 10.000 KRW được chọn 1 đồ uống miễn phí: soda, Americano hoặc trà đá.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '긱스타 PC방' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '현금 또는 계좌이체 10,000원 충전 시 10시간+음료증정(아이스티/다방커피/아메리카노/탄산)', 'Pay 10,000 KRW by cash or bank transfer to receive 10 hours plus 1 free drink. Drink options: iced tea, dabang-style coffee, Americano, or soda.', '現金または口座振込で10,000ウォンをチャージすると、10時間＋ドリンク1杯を提供。 ドリンクはアイスティー・ダバンコーヒー・アメリカーノ・炭酸飲料から選択。', 'Nạp 10.000 KRW bằng tiền mặt hoặc chuyển khoản được tặng 10 giờ sử dụng và 1 đồ uống. Có thể chọn đồ uống: trà đá, cà phê kiểu dabang, Americano hoặc soda.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '에이플 PC방' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '아메리카노 빅사이즈 2잔'' 또는 ''아이스티 2잔'' 또는 ''아메리카노 빅사이즈 1잔 + 아이스티 1잔'' 주문 시 두 잔 중 한 잔에 샷 추가 1회 무료', 'Free extra shot in one of the two drinks when ordering either 2 Big Americanos, 2 Iced Teas, or 1 Big Americano plus 1 Iced Tea.', 'ビッグサイズアメリカーノ2杯、アイスティー2杯、またはビッグサイズアメリカーノ1杯＋アイスティー1杯注文時、2杯のうち1杯にショット1回無料追加。', 'Gọi 2 Americano cỡ lớn, 2 trà đá hoặc 1 Americano cỡ lớn + 1 trà đá được thêm 1 shot miễn phí vào 1 trong 2 ly.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '카페 봄봄' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '게임비 15% 할인', '15% off game fees.', 'ゲーム料金15%割引。', 'Giảm 15% phí chơi game.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '히어로 보드게임 카페' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '계좌이체 또는 카운터에서 현금으로 10,000원 충전 후 사장님께 문자로 "AI대학 재학생인데, 10,000원 충전했습니다"라고 전송 시 노래 5곡 추가 제공', 'Pay 10,000 KRW by bank transfer or cash at the counter, then text the owner “I’m an AI College student, and I recharged 10,000 KRW” to get 5 extra songs.', '口座振込またはカウンターで現金10,000ウォンをチャージ後、店主に「AI大学の在学生で、10,000ウォンチャージしました」と送信すると、5曲追加提供。', 'Nạp 10.000 KRW bằng chuyển khoản hoặc tiền mặt tại quầy, sau đó nhắn cho chủ quán “Tôi là sinh viên College of AI và đã nạp 10.000 KRW” để được tặng thêm 5 bài hát.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블라블라 코인노래방' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '음료 10% 할인', 'Get 10% off all drinks.', 'すべてのドリンクが10%割引。', 'Giảm 10% cho tất cả đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = 'eea cafe' AND c.name_ko = 'AI대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '음료 1병 제공', 'Free 1 bottled soft drink.', 'ボトル飲料1本を無料提供。', 'Tặng 1 chai nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '광어삼촌' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '식사류 3개 이상 주문시 음료 1캔 제공', 'Free 1 canned soft drink when ordering 3 or more meals.', '食事メニュー3品以上注文時、缶ドリンク1本を無料提供。', 'Gọi từ 3 món ăn trở lên được tặng 1 lon nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '논두렁갈비' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '토스 or 카카오페이 결제 시 5% 할인 (고기류 주문 시에만 해당)', '5% off when paying with Toss or KakaoPay (meat dishes only).', 'TossまたはKakaoPay決済時、肉料理5%割引。', 'Giảm 5% món thịt khi thanh toán bằng Toss hoặc KakaoPay.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '먹돼지' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2시반에서 5시반 사이 주문 시, 토핑(더블치즈, 옥수수콘, 콩, 할라피뇨) 추가', 'Get one free topping add-on (Double Cheese, Corn, Beans, or Jalapeños) for orders placed between 2:30 PM and 5:30 PM.', '2:30 PM〜5:30 PMの注文で、ダブルチーズ・コーン・豆・ハラペーニョのうちトッピング1つを無料追加。', 'Đơn đặt từ 2:30 PM đến 5:30 PM được thêm miễn phí 1 topping: phô mai đôi, bắp, đậu hoặc ớt jalapeño.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '부리또집' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '닭한마리, 만두전골 인원 수 맞춰서 주문 시 음료수 한 캔 제공', 'Free 1 canned soft drink when ordering Dakhanmari or dumpling hot pot according to the number of people.', 'タッカンマリまたは餃子鍋を人数分注文時、缶ドリンク1本を無料提供。', 'Gọi dakhanmari hoặc lẩu mandu theo số người được tặng 1 lon nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '황새골' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 주문 시 음료 한 캔 제공 or 4인 이상 방문하여 5만원 이상 주문 시 소주 한 병 제공', 'For groups of 4 or more, get 1 free canned soft drink. Or, for groups of 4 or more spending 50,000 KRW or more, get 1 free bottle of soju.', '4人以上で缶ドリンク1本を無料提供。
 また、4人以上で50,000ウォン以上利用時、焼酎1本を無料提供。', 'Nhóm từ 4 người trở lên được tặng 1 lon nước ngọt.
 Hoặc nhóm từ 4 người trở lên chi từ 50.000 KRW trở lên được tặng 1 chai soju.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '신의주 찹쌀순대' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4만원 이상 구매 시 주먹밥 제공', 'Free rice ball for purchases of 40,000 KRW or more.', '40,000ウォン以上の購入でおにぎりを無料提供。', 'Mua từ 40.000 KRW trở lên được tặng cơm nắm.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '불타는 소금구이' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2만원 이상 주문 시 음료 2개 제공, 3만원 이상 주문 시 단품을 세트로 변경', 'For orders of 20,000 KRW or more, get 2 free drinks. For orders of 30,000 KRW or more, upgrade single items to set menus.', '20,000ウォン以上の注文でドリンク2杯を無料提供。
 30,000ウォン以上の注文で単品をセットメニューに変更。', 'Đơn từ 20.000 KRW trở lên được tặng 2 đồ uống.
 Đơn từ 30.000 KRW trở lên được nâng món lẻ thành set menu.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '지지고' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 방문 시 500ml 음료 제공', 'Free 1 500ml soft drink for groups of 4 or more.', '4人以上の来店で1 500ml ソフトドリンクを無料提供。', 'Nhóm từ 4 người trở lên được tặng 1 500ml nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '맛닭꼬' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '테이블 당 2인분 이상 주문 시 치즈 추가', 'Free cheese topping when ordering 2 or more servings per table.', 'テーブルごとに2人前以上注文時、チーズトッピングを無料追加。', 'Mỗi bàn gọi từ 2 phần trở lên được thêm topping phô mai miễn phí.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '코웍 비스트로' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '메뉴당 닭가슴살 or 훈제오리 30g 추가', 'Add 30g of chicken breast or smoked duck per menu item.', 'メニュー1品ごとに、鶏むね肉または燻製鴨30gを追加提供。', 'Thêm 30g ức gà hoặc vịt hun khói cho mỗi món.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '포케올데이' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3만원 이상 주문 시 펩시 제로 1.25L 제공', 'Free 1.25L Pepsi Zero for orders of 30,000 KRW or more.', '30,000ウォン以上の注文でペプシゼロ1.25Lを無料提供。', 'Đơn từ 30.000 KRW trở lên được tặng 1 chai Pepsi Zero 1,25L.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '피자스쿨' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '테이블 당 음료 1캔 제공', 'Free 1 canned soft drink per table.', 'テーブルごとに1 缶飲料を無料提供。', 'Tặng 1 nước ngọt đóng lon cho mỗi bàn.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '리얼후라이' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2만원 이상 주문 시 음료 1캔 제공', 'Free 1 canned soft drink for orders of 20,000 KRW or more.', '20,000ウォン以上の注文で缶ドリンク1本を無料提供。', 'Đơn từ 20.000 KRW trở lên được tặng 1 lon nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '왕돈까스왕냉면' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 방문 시 테이블 당 음료 500ml or 군만두 4개 중 택 1 제공', 'For groups of 4 or more, choose one per table: 1 500ml soft drink or 4 fried dumplings.', '4人以上の来店で、テーブルごとに1 500ml ソフトドリンクまたは4 焼き餃子のいずれかを提供。', 'Nhóm từ 4 người trở lên chọn 1 ưu đãi cho mỗi bàn: 1 500ml nước ngọt hoặc 4 mandu chiên.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '취향' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '더블치즈버거세트 9,900원으로 할인, 단품 포장 주문 시 컵 음료 제공(포장에만 해당)', 'Double Cheeseburger Set discounted to 9,900 KRW, plus a free cup drink for takeout single-item orders only.', 'ダブルチーズバーガーセットは9,900ウォン。単品テイクアウト注文時はカップドリンク無料。', 'Set Double Cheeseburger giá 9.900 KRW, kèm đồ uống ly miễn phí chỉ cho đơn món lẻ mang đi.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '크라이치즈버거' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4만원 이상 주문 시, 음료 1캔 제공', 'Get 1 free canned soft drink with orders of 40,000 KRW or more.', '40,000ウォン以上の注文で、缶ドリンク1本を無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng 1 lon nước ngọt.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '파동추야' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '칵테일 4잔 이상 주문 시 테이블 당 미니나쵸 제공', 'Free mini nachos per table when ordering 4 or more cocktails.', 'カクテル4杯以上注文時、テーブルごとにミニナチョスを無料提供。', 'Gọi từ 4 ly cocktail trở lên được tặng nachos nhỏ cho mỗi bàn.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '가치' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '18시 이전 주문 시, 테이블 당 소주 1병 제공', 'Orders before 6 PM: 1 free bottle of soju per table.', '18時以前の注文で、テーブルごとに焼酎1本を無料提供。', 'Đặt món trước 6 PM được tặng 1 chai soju cho mỗi bàn.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '28청춘호프포차' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '7만원 이상 현금 결제 시 김치볶음밥 or 계란말이 중 택 1 제공', 'For cash payments of 70,000 KRW or more, choose one free item: Kimchi Fried Rice or Rolled Omelet.', '70,000ウォン以上の現金決済で、キムチチャーハンまたは卵焼きのうち1つを提供。', 'Thanh toán tiền mặt từ 70.000 KRW trở lên được chọn 1 món: cơm chiên kimchi hoặc trứng cuộn.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블루힐' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '결제금액 5% 할인(술값 포함) + 11시-17시 & 2인 이상 방문 시 음료 1캔 제공(현금, 계좌이체 시)', '5% off total payment, including alcohol, plus 1 free canned soft drink for groups of 2 or more visiting between 11 AM and 5 PM. Cash or bank transfer only.', '酒類を含む決済金額5%割引。さらに11時〜17時に2人以上で来店時、缶飲料1本提供。現金または口座振込のみ対象。', 'Giảm 5% tổng thanh toán, bao gồm đồ uống có cồn. Ngoài ra, nhóm từ 2 người đến từ 11 AM đến 5 PM được tặng 1 lon nước ngọt. Chỉ áp dụng khi thanh toán tiền mặt hoặc chuyển khoản.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도로 3가' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4만원 이상 결제 시 소주 1병 제공', 'Free 1 bottle of soju for payments of 40,000 KRW or more.', '40,000ウォン以上の決済で焼酎1本を無料提供。', 'Thanh toán từ 40.000 KRW trở lên được tặng 1 chai soju.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '숯가마 바베큐 치킨' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만원 이상 결제 시 갈릭 감자튀김 미니/수제 소시지 중 택1', 'For payments of 50,000 KRW or more, choose one complimentary item: Mini Garlic Fries or Handmade Sausage.', '50,000ウォン以上の支払いで、ミニガーリックフライドポテトまたは手作りソーセージのうち1つを無料提供。', 'Thanh toán từ 50.000 KRW trở lên được chọn miễn phí 1 trong 2 món: khoai tây chiên bơ tỏi cỡ nhỏ hoặc xúc xích thủ công.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '역전할머니맥주' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '청소년 요금제로 이용 가능', 'Available at youth rates.', '青少年料金で利用可能。', 'Có thể sử dụng với mức giá dành cho thanh thiếu niên.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '놀숲' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '아이스티 or 아메리카노 빅사이즈 2잔 주문 시 1잔 샷 추가', 'Free extra shot in one drink when ordering 2 big-size iced teas or Americanos.', 'ビッグサイズのアイスティーまたはアメリカーノを2杯注文時、1杯にショット1回無料追加。', 'Gọi 2 ly trà đá hoặc Americano cỡ lớn được thêm 1 shot miễn phí vào 1 ly.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '카페 봄봄' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '18,000원 이상 방문 주문 시, 초코/딸기쉘, 그레놀라, 후르트링, 콘프로스트, 첵스초코, 식빵 중 택 1', 'For in-store orders of 18,000 KRW or more, choose one topping: chocolate shell, strawberry shell, granola, Froot Loops, Corn Frost, Choco Chex, or bread.', '18,000ウォン以上の来店注文で、チョコシェル・いちごシェル・グラノーラ・フルートループ・コーンフロスト・チョコチェックス・食パンのうち1つを選択。', 'Đơn tại cửa hàng từ 18.000 KRW trở lên được chọn 1 topping: sốt phủ chocolate, sốt phủ dâu, granola, Froot Loops, Corn Frost, Choco Chex hoặc bánh mì.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '요아정' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '전 메뉴 10% 할인 (세트메뉴 제외)', '10% off all menu items, excluding set menus.', 'セットメニューを除く全メニュー10%割引。', 'Giảm 10% toàn bộ menu, không gồm set menu.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '에브어이브' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '이용료 15% 할인', '15% off usage fees.', '利用料金15%割引。', 'Giảm 15% phí sử dụng.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '히어로 보드게임 카페' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '모든 음료 주문 시 타피오카펄 무료 추가 (폼, 다른 토핑류 X)
1. 점보 사이드도 펄 라지양으로 제공
2. 중복 혜택 적용 불가(공차데이, 통신사 할인폭이 큰 이벤트의 경우)
*학생증 증명 필수(동행까지 적용 가능)', 'Get free tapioca pearls with any drink order. Foam and other toppings are excluded.
1. For jumbo-size drinks, the amount of pearls provided will be the same as for a large size.
2. Cannot be combined with other promotions, including Gong Cha Day or mobile carrier promotions offering greater discounts.
*Student ID verification is required. The benefit also applies to accompanying guests.', 'すべてのドリンク注文時、タピオカパールを無料追加。フォームやその他のトッピングは対象外。
1. ジャンボサイズでも、パールはラージサイズ分を提供。
2. ゴンチャデーや通信会社の割引率が高いキャンペーンなど、他の特典との併用不可。
*学生証の提示必須。同伴者にも適用可能。', 'Khi gọi bất kỳ đồ uống nào, được thêm trân châu tapioca miễn phí. Không áp dụng cho lớp foam hoặc các loại topping khác.
1. Với đồ uống size Jumbo, lượng trân châu được tặng tương đương size Large.
2. Không áp dụng đồng thời với các ưu đãi khác, bao gồm Gong Cha Day hoặc chương trình giảm giá cao hơn của nhà mạng.
*Bắt buộc xuất trình thẻ sinh viên. Ưu đãi cũng áp dụng cho người đi cùng.', '2026-09-01', '2027-03-01', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '공차' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '10시간 충전 시, 2시간+음료 1잔 제공', 'Pay for 10 hours and receive 2 bonus hours plus 1 free drink.', '10時間分の決済で、2時間追加＋ドリンク1杯を提供。', 'Thanh toán 10 giờ được tặng thêm 2 giờ và 1 đồ uống.', '2026-09-01', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '에이플 PC방' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '음료 4잔 주문시, 아메리카노 1잔 제공', 'Get 1 free Americano when ordering 4 drinks.', 'ドリンクを4杯注文すると、アメリカーノ1杯を無料提供。', 'Gọi 4 đồ uống được tặng 1 ly Americano.', '2026-09-01', '2026-10-01', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '메가커피' AND c.name_ko = '공과대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '고기류 주문 후 현금 or 계좌 이체 or 카카오페이 결제 시 5% 할인', '5% off meat dishes when paying by cash, bank transfer, or KakaoPay.', '肉料理注文後、現金・口座振込・KakaoPay決済時5%割引。', 'Giảm 5% món thịt khi thanh toán bằng tiền mặt, chuyển khoản hoặc KakaoPay.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '먹돼지' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2인 이하 방문 시 음료수 1병 증정 및 3인 이상 4인 이하 방문 시 음료수 2병 증정', 'For groups of 1–2, get 1 bottled soft drink. For groups of 3–4, get 2 bottled soft drinks.', '1〜2人でボトルドリンク1本を無料提供。
 3〜4人で2 ボトルドリンクを無料提供。', 'Nhóm 1–2 người được tặng 1 chai nước ngọt.
 Nhóm 3–4 người được tặng 2 nước ngọt đóng chai.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '면식당' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '메인 메뉴 두 개 이상 주문 시 음료수 1병 증정', 'Free bottled soft drink when ordering 2 or more main menu items.', '2つ以上 メインメニュー注文時、ボトルドリンクを無料提供。', 'Khi gọi 2 trở lên món chính, được tặng nước ngọt đóng chai.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '고추동 제면소' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '영수증 3개 이하 합산 후 30,000원 이상 주문 시 유토우 1pc or 빙홍차 1병 or 차파이 1병 중 택 1 증정', 'For purchases of 30,000 KRW or more, choose one: Bing Hong Cha, Cha Pai, or 3 pieces of Youtiao. Up to 3 receipts may be combined.', '30,000ウォン以上の購入で、油条3個・茶π・冰紅茶のうち1つを選択。レシートは最大3枚まで合算可能。
 レシートは最大3枚まで合算可能。', 'Đơn mua từ 30.000 KRW trở lên được chọn 1 trong các món: 3 miếng quẩy Youtiao, Cha Pai hoặc Bing Hong Cha. Có thể gộp tối đa 3 hóa đơn.
 Có thể gộp tối đa 3 hóa đơn.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '샹츠마라' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '음료수 1병 증정 및 네이버 리뷰 작성 시 비빔면 증정', 'Free bottled soft drink plus free Bibim Noodles with a Naver review.', 'ボトルドリンク1本を無料提供。Naverレビュー作成時、ビビン麺を無料提供。', 'Tặng 1 chai nước ngọt. Viết review Naver được tặng mì trộn bibim.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도동 솥뚜껑' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '음료수 1병 증정 or 40,000원 이상 주문 시 케이준 프라이 증정', 'Free bottled soft drink, or free Cajun Fries for orders of 40,000 KRW or more.', '40,000ウォン以上の注文でボトルドリンク、またはケイジャンポテトを無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng nước ngọt đóng chai, hoặc khoai tây Cajun.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = 'BHC 치킨' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4명 이상 방문 시 음료수 1병 증정 및 50,000원 이상 주문 시 소주 1병 증정', 'Free bottled soft drink for groups of 4 or more, plus 1 free bottle of soju for orders of 50,000 KRW or more.', '4人以上の来店でボトルドリンク1本を無料提供。
 50,000ウォン以上の注文で焼酎1本を無料提供。', 'Nhóm từ 4 người trở lên được tặng 1 chai nước ngọt.
 Đơn từ 50.000 KRW trở lên được tặng 1 chai soju.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '신의주 찹쌀순대' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3인 이상 주문 시 음료캔 1개 제공 / 6인 이상 주문 시 음료캔 2개 제공 / 9인 이상 주문 시 음료캔 3개 제공', 'Free canned drinks based on order size: 1 can for orders of 3 or more people, 2 cans for 6 or more, and 3 cans for 9 or more.', '人数に応じて缶飲料を無料提供：3人は1缶、6人は2缶、9人は3缶。', 'Tặng nước lon theo số người/khẩu phần: 1 lon cho 3 người, 2 lon cho 6 người, 3 lon cho 9 người.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '삼봉버거' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '40,000원 이상 주문 시 소주 1병 증정', 'Get 1 free bottle of soju with orders of 40,000 KRW or more.', '40,000ウォン以上の注文で焼酎1本を無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng 1 chai soju.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '숯가마 바베큐 치킨' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '매장 방문 인원 수 이상 닭갈비 주문 시 치즈사리 증정 (ex. 4인 방문 시 4인분 이상 주문)', 'Free cheese add-on when ordering Dakgalbi equal to the number of visitors. For example, order 4 servings for 4 people.', '来店人数以上のタッカルビを注文時、チーズ追加を無料提供。例：4人来店時は4人前以上注文。', 'Gọi dakgalbi bằng hoặc nhiều hơn số khách đến cửa hàng sẽ được tặng phần phô mai thêm. Ví dụ: 4 người thì gọi từ 4 phần trở lên.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '코웍 비스트로' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '더블치즈버거세트 1,000원 할인', '1,000 KRW discount on Double Cheeseburger Set', 'ダブルチーズバーガーセット 1,000ウォン割引', 'Giảm 1.000 KRW cho Set Double Cheeseburger', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '크라이치즈버거' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '현금 or 계좌이체로 결제 시 5% 할인', '5% off when paying by cash or bank transfer.', '現金または口座振込決済時、5%割引。', 'Giảm 5% khi thanh toán bằng tiền mặt hoặc chuyển khoản.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도로 3가' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '20,000원 이상 주문 시 즉석라면 증정', 'Get 1 free ramyeon with orders of 20,000 KRW or more.', '20,000ウォン以上の注文でラーメン1杯を無料提供。', 'Đơn từ 20.000 KRW trở lên được tặng 1 phần mì ramyeon.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '씨밤' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '50,000원 이상 주문 시 갈릭빠다포테이토 mini or 수제소시지 중 택 1 증정', 'For orders of 50,000 KRW or more, choose one: Garlic Butter Potatoes or Handmade Sausage. The Garlic Butter Potatoes are served in a smaller-than-standard portion.', '50,000ウォン以上の注文で、ガーリックバターポテトまたは手作りソーセージのうち1つを提供。ガーリックバターポテトは通常より少なめの量で提供。', 'Đơn từ 50.000 KRW trở lên được chọn 1 trong 2 món: khoai tây bơ tỏi hoặc xúc xích thủ công. Phần khoai tây bơ tỏi có khẩu phần nhỏ hơn thông thường.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '역전할머니맥주' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '팀 당 50,000원 이상 주문 시 소주 1병 증정', 'Free 1 bottle of soju for orders of 50,000 KRW or more.', '50,000ウォン以上の注文で焼酎1本を無料提供。', 'Đơn từ 50.000 KRW trở lên được tặng 1 chai soju.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '젠사이야' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '70,000원 이상 현금 결제 시 김치볶음밥 or 계란말이 or 칵테일 소주(420ml) 택 1 증정', 'For cash payments of 70,000 KRW or more, choose one: Kimchi Fried Rice, Rolled Omelet, or Cocktail Soju (420ml).', '70,000ウォン以上の現金決済で、キムチチャーハン・卵焼き・カクテル焼酎（420ml）のうち1つを選択。', 'Thanh toán tiền mặt từ 70.000 KRW trở lên được chọn 1 món: cơm chiên kimchi, trứng cuộn hoặc soju cocktail 420ml.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블루힐' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '40,000원 이상 주문 시 감자튀김 증정', 'Free French Fries for orders of 40,000 KRW or more.', '40,000ウォン以上の注文でフライドポテトを無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng khoai tây chiên.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '사랑과 평화' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '빅아메리카노 2잔 or 빅아메리카노 1잔 + 아이스티 1잔 주문 시 샷 추가 무료 및 봄봄오더로 5,000원 이상 주문 시 샷 추가 무료', 'Free extra shot in one of the two drinks when ordering either 2 Big Americanos, 2 Iced Teas, or 1 Big Americano plus 1 Iced Tea.', 'ビッグサイズアメリカーノ2杯、アイスティー2杯、またはビッグサイズアメリカーノ1杯＋アイスティー1杯注文時、2杯のうち1杯にショット1回無料追加。', 'Gọi 2 Americano cỡ lớn, 2 trà đá hoặc 1 Americano cỡ lớn + 1 trà đá được thêm 1 shot miễn phí vào 1 trong 2 ly.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '카페 봄봄' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '모든 음료 주문 시 타피오카펄 무료 추가 (폼, 다른 토핑류 X)
1. 점보 사이드도 펄 라지양으로 제공
2. 중복 혜택 적용 불가(공차데이, 통신사 할인폭이 큰 이벤트의 경우)
*학생증 증명 필수(동행까지 적용 가능)', 'Free tapioca pearls with any drink order. Foam and other toppings are excluded.
For jumbo drinks, the amount of pearls provided will be the same as a large size.
Student ID verification is required. The benefit may also be applied to companions.
Cannot be combined with other promotions, including Gong Cha Day or mobile carrier promotions offering higher discounts.', 'すべてのドリンク注文時、タピオカパールを無料追加。フォームやその他のトッピングは対象外。
ジャンボサイズは、ラージサイズ分のパールを提供。
学生証の提示必須。同伴者にも適用可能。
ゴンチャデーや通信会社の割引率が高いキャンペーンなど、他の特典との併用不可。', 'Khi gọi bất kỳ đồ uống nào, được thêm trân châu tapioca miễn phí. Không áp dụng cho lớp foam hoặc các loại topping khác.
Với size Jumbo, lượng trân châu được tặng tương đương size Large.
Bắt buộc xuất trình thẻ sinh viên; ưu đãi có thể áp dụng cho cả người đi cùng.
Không áp dụng đồng thời với ưu đãi khác, bao gồm Gong Cha Day hoặc chương trình giảm giá cao của nhà mạng.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '공차' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '18,000원 이상 구매 시 초코쉘 or 딸기쉘 or 그래놀라 중 택 1 증정', 'For purchases of 18,000 KRW or more, choose one: Chocolate Shell, Strawberry Shell, or Granola.', '18,000ウォン以上で、チョコシェル・いちごシェル・グラノーラのうち1つを選択。', 'Từ 18.000 KRW trở lên được chọn 1 trong các món: sốt phủ chocolate, sốt phủ dâu hoặc granola.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '요아정' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5,000원 시간 충전 시 탄산음료 증정 및 10,000원 시간 충전 시 카페음료 증정', 'Get a free soda when adding 5,000 KRW of credit. Get a free café drink when adding 10,000 KRW of credit.', '5,000ウォンチャージで炭酸飲料を無料提供。
 10,000ウォンチャージでカフェドリンクを無料提供。', 'Nạp 5.000 KRW được tặng soda.
 Nạp 10.000 KRW được tặng đồ uống cà phê.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '긱스타 PC방' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '세트 A, B, C를 청소년 이용금액으로 적용
set A 5,500원
set B 8,500원
set C 10,500원', 'Set A, B, and C available at youth rates: Set A 5,500 KRW, Set B 8,500 KRW, Set C 10,500 KRW.', 'Set A・B・Cは青少年料金で利用可能：Set A 5,500ウォン、Set B 8,500ウォン、Set C 10,500ウォン。', 'Set A, B, C áp dụng giá thanh thiếu niên: Set A 5.500 KRW, Set B 8.500 KRW, Set C 10.500 KRW.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '놀숲' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '현금 or 계좌이체로 10,000원 결제 시 10시간 충전 및 탄산음료 or 아이스티 or 아이스 아메리카노 중 택 1 증정', 'Pay 10,000 KRW by cash or bank transfer to receive 10 hours plus 1 free drink. Drink options: iced tea, dabang-style coffee, Americano, or soda.', '現金または口座振込で10,000ウォンをチャージすると、10時間＋ドリンク1杯を提供。 ドリンクはアイスティー・ダバンコーヒー・アメリカーノ・炭酸飲料から選択。', 'Nạp 10.000 KRW bằng tiền mặt hoặc chuyển khoản được tặng 10 giờ sử dụng và 1 đồ uống. Có thể chọn đồ uống: trà đá, cà phê kiểu dabang, Americano hoặc soda.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '에이플 PC방' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '게임 비용 15% 할인', '15% off game fees.', 'ゲーム料金15%割引。', 'Giảm 15% phí chơi game.', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '히어로 보드게임 카페' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '인원별 이용료 추가 할인 및 테이블 당 갓 구운 와플 증정
2-3인: 2,000원 할인
4-5인: 4,000원 할인
6인 이상: 6,000원 할인', 'dditional group discounts on usage fee & free freshly baked waffle per table
2–3 people: 2,000 KRW discount
4–5 people: 4,000 KRW discount
6+ people: 6,000 KRW discount', '人数に応じた利用料金の 추가 割引＆テーブルごとに焼きたてワッフルプレゼント
2〜3人：2,000ウォン割引
4〜5人：4,000ウォン割引
6人以上：6,000ウォン割引', 'Giảm giá thêm phí sử dụng theo số lượng người & tặng bánh Waffle vừa nướng cho mỗi bàn
2–3 người: Giảm 2.000 KRW
4–5 người: Giảm 4.000 KRW
Từ 6 người trở lên: Giảm 6.000 KRW', '2026-09-08', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '와룸 보드게임카페 서울대입구역점' AND c.name_ko = '사회과학대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4인 이상 1인 1메뉴 주문 시 음료(500ml) or 군만두 4개 제공', 'For tables of 4 or more, choose either 1 fried dumpling per person or 1 500ml drink.', '4人以上のテーブル注文で、1人につき焼き餃子1個、または500mlドリンク1本のうちいずれかを提供。', 'Bàn từ 4 người trở lên được chọn 1 trong 2 ưu đãi: mỗi người 1 bánh mandu chiên hoặc 1 chai đồ uống 500ml.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '취향' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '닭갈비 치즈 추가 무료', 'Free cheese add-on per table when ordering 2 or more servings of Dakgalbi.', 'タッカルビ2人前以上注文時、テーブルごとにチーズ追加を無料提供。', 'Khi gọi từ 2 phần dakgalbi trở lên, mỗi bàn được tặng phần phô mai thêm.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '코웍 비스트로' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '테이블당 메뉴 2개 이상 주문 시 음료 서비스
*점심 12~2시, 저녁 5~7시 제외', 'Get 1 free drink per table when ordering at least 2 menu items.
*Not available from 12 PM to 2 PM or from 5 PM to 7 PM.', '1テーブルにつき、メニューを2品以上注文するとドリンク1杯を無料提供。
*12時〜14時および17時〜19時は対象外。', 'Mỗi bàn gọi từ 2 món trở lên được tặng 1 đồ uống.
*Không áp dụng từ 12:00 đến 14:00 và từ 17:00 đến 19:00.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '고추동 제면소' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3만원 이상 구매 시 유토우 튀김(중) 또는 캔음료 제공', 'For purchases of 30,000 KRW or more, get free Youtiao (M) or a canned drink.', '30,000ウォン以上のお買い上げで、油条(中)または缶ドリンク1本をサービス。', 'Đơn từ 30.000 KRW trở lên tặng quẩy Youtiao (vừa) hoặc 1 lon nước ngọt.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '샹츠마라' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1인 1메뉴 이상 주문 시 테이블당 음료 1캔 제공', 'Free canned drink per table with at least 1 menu item per person.', '1人1品以上のご注文でテーブルごとに缶ドリンク1本を無料提供。', 'Gọi từ 1 món/người trở lên tặng 1 lon nước ngọt cho mỗi bàn.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '나비루' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '30,000원 이상 주문 시 펩시 제로 1.25L 제공', 'Free 1.25L Pepsi Zero for every 30,000 KRW spent.', '30,000ウォンごとの利用で1.25L ペプシゼロを無料提供。', 'Tặng 1.25L Pepsi Zero cho mỗi 30.000 KRW chi tiêu.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '피자스쿨' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '20,000원 이상 주문 시 음료 1캔 제공', 'Get 1 free canned soft drink with orders of 20,000 KRW or more.', '20,000ウォン以上の注文で、缶ドリンク1本を無料提供。', 'Đơn từ 20.000 KRW trở lên được tặng 1 lon nước ngọt.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '왕돈까스왕냉면' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '메인 메뉴 2개 이상 주문 시 캔 음료 제공', 'Free canned drink with an order of 2 or more main dishes.', 'メイン料理2品以上のご注文で缶ドリンク1本を無料提供。', 'Gọi từ 2 món chính trở lên tặng 1 lon nước ngọt.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '슬로우캘리' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3인 주문 시 음료 1개 제공 / 4인 이상 주문 시 음료 2개 제공', 'Groups of 2 or more get 1 free drink.
Groups of 4 get 2 free drinks.', '2人以上の注文でドリンク1杯、4人での注文でドリンク2杯を無料提供。', 'Đơn dành cho từ 2 người trở lên được tặng 1 đồ uống; đơn dành cho 4 người được tặng 2 đồ uống.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '핵밥' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1인당 음료수 1캔 or 해쉬브라운 2p 제공', 'Each person may choose one complimentary option: 1 canned soft drink or 2 hash browns.', '1人につき、缶ドリンク1本またはハッシュブラウン2個のうち1つを無料提供。', 'Mỗi người được chọn miễn phí 1 lon nước ngọt hoặc 2 miếng hash brown.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '미태리' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '현금 or 계좌이체 시 메뉴당 500원 할인', '500 KRW off per menu item when paying by cash or bank transfer.', '現金または口座振込決済時、メニュー1品につき500ウォン割引。', 'Giảm 500 KRW cho mỗi món khi thanh toán bằng tiền mặt hoặc chuyển khoản.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '손칼국수' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '더블치즈버거세트 9,900원 + 단품 포장 주문 시 컵 음료 제공 (포장에만 해당)', 'Double Cheeseburger Set for 9,900 KRW, plus a free cup drink for takeout single-item orders only.', 'ダブルチーズバーガーセットは9,900ウォン。単品テイクアウト注文時はカップドリンク無料。', 'Set Double Cheeseburger giá 9.900 KRW, kèm đồ uống ly miễn phí chỉ cho đơn món lẻ mang đi.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '크라이치즈버거' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '3인 이상 방문 시 주먹밥 제공', 'Free rice ball for groups of 3 or more.', '3人以上の来店でおにぎりを無料提供。', 'Nhóm từ 3 người trở lên được tặng cơm nắm.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '불타는 소금구이' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만원 이상 주문 시 음료수 1병 or 소주 1병 서비스', 'For orders of 50,000 KRW or more, choose one: 1 bottled soft drink or 1 bottle of soju.', '50,000ウォン以上で、ボトルドリンク1本または焼酎1本のうち1つを選択。', 'Từ 50.000 KRW trở lên được chọn 1 trong các món: 1 chai nước ngọt hoặc 1 chai soju.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '광어삼촌' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '테이블 당 음료 1캔 제공', 'Free 1 canned soft drink per table.', 'テーブルごとに1 缶飲料を無料提供。', 'Tặng 1 nước ngọt đóng lon cho mỗi bàn.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '리얼후라이' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '부리또 주문 시 토핑 추가 (베이컨 제외)', 'Free topping add-on for burrito orders, excluding bacon.', 'ブリトー注文時、ベーコンを除くトッピング1つ無料。', 'Gọi burrito được thêm 1 topping miễn phí, trừ bacon.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '밀플랜비' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '토스 or 카카오페이 결제 시 5% 할인 (고기류 주문 시에만 해당)', '5% off when paying with Toss or KakaoPay, applicable to meat dishes only.', 'TossまたはKakaoPay決済時、肉料理5%割引。', 'Giảm 5% món thịt khi thanh toán bằng Toss hoặc KakaoPay.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '먹돼지' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '리뷰이벤트 참여 시 사이드메뉴(비빔면) + 음료 1개 제공', 'Free side menu (Bibim Noodles) plus 1 drink when participating in the review event.', 'レビューイベント参加時、サイドメニュー（ビビン麺）＋ドリンク1杯を無料提供。', 'Tham gia sự kiện review được tặng món phụ (mì trộn bibim) + 1 đồ uống.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도동 솥뚜껑' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2만원 이상 주문 시 음료 2개 서비스, 3만원 이상 주문 시 모든 메뉴 세트로 변경', 'For orders of 20,000 KRW or more, get 2 free drinks. For orders of 30,000 KRW or more, upgrade all menu items to set menus.', '20,000ウォン以上の注文でドリンク2杯を無料提供。
 30,000ウォン以上の注文で全メニューをセットメニューに変更。', 'Đơn từ 20.000 KRW trở lên được tặng 2 đồ uống.
 Đơn từ 30.000 KRW trở lên được nâng toàn bộ menu thành set menu.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '지지고' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2만원 이상 주문 시 즉석 라면 제공', 'Free instant ramen for orders of 20,000 KRW or more.', '20,000ウォン以上の注文でインスタントラーメンを無料提供。', 'Đơn từ 20.000 KRW trở lên được tặng ramen ăn liền.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '씨밤' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '칵테일 4잔 이상 주문시 테이블 당 미니 나초', 'Free mini nachos per table when ordering 4 or more cocktails.', 'カクテル4杯以上注文時、テーブルごとにミニナチョスを無料提供。', 'Gọi từ 4 ly cocktail trở lên được tặng nachos nhỏ cho mỗi bàn.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '가치' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만원 이상 주문 시 소주 1병', 'Free 1 bottle of soju for orders of 50,000 KRW or more.', '50,000ウォン以上の注文で焼酎1本を無料提供。', 'Đơn từ 50.000 KRW trở lên được tặng 1 chai soju.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '젠사이야' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '메뉴 2개 이상 주문 시 현금 or 계좌이체 or 카카오페이 결제 시 5% 할인', 'Free 1 bottle of soju for cash payments of 50,000 KRW or more.', '50,000ウォン以上の現金決済で焼酎1本を無料提供。', 'Thanh toán tiền mặt từ 50.000 KRW trở lên được tặng 1 chai soju.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '인쌩맥주' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '7만원 이상 현금 결제 시 김치볶음밥 또는 계란말이 제공', 'For cash payments of 70,000 KRW or more, choose one: Kimchi Fried Rice or Rolled Omelet.', '70,000ウォン以上の現金決済で、キムチチャーハンまたは卵焼きのうち1つを提供。', 'Thanh toán tiền mặt từ 70.000 KRW trở lên được chọn 1 món: cơm chiên kimchi hoặc trứng cuộn.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블루힐' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '현금 or 계좌 이체 시 5% 할인', 'Free French Fries for orders of 40,000 KRW or more per table.', '40,000ウォン以上の注文でフライドポテトを無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng khoai tây chiên.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '사랑과 평화' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '현금 or 계좌 이체 시 5% 할인', '5% off when paying by cash or bank transfer.', '現金または口座振込決済時、5%割引。', 'Giảm 5% khi thanh toán bằng tiền mặt hoặc chuyển khoản.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도로 3가' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4만원 이상 주문 시 음료 1캔(350ml) 제공', 'Free 1 350ml canned soft drink for orders of 40,000 KRW or more.', '40,000ウォン以上の注文で1 350ml 缶ドリンクを無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng 1 350ml nước ngọt đóng lon.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '파동추야' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '만원 이상 주문 시 샷 추가', 'Free extra espresso shot for orders of 10,000 KRW or more.', '10,000ウォン以上の注文でエスプレッソショット1回無料追加。', 'Đơn từ 10.000 KRW trở lên được thêm 1 shot espresso miễn phí.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블랙&조이커피' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2잔 (빅 아메리카노 2잔/빅 아메 1잔, 아이스티 1잔) 주문 시 샷 추가 1번/봄봄 오더로 5천원 주문 시 샷 추가 무료
*평일 오후 3시 이후, 주말 모든 시간대 가능', 'Free extra shot once when ordering 2 drinks (2 Big Americanos, or 1 Big Americano plus 1 Iced Tea). 
Free extra shot for orders of 5,000 KRW or more through Bombom Order.', 'ドリンク2杯（ビッグサイズアメリカーノ2杯、またはビッグサイズアメリカーノ1杯＋アイスティー1杯）注文時、1杯にショット1回無料追加。Bombom Orderで5,000ウォン以上注文時もショット1回無料追加。', 'Gọi 2 đồ uống (2 Americano cỡ lớn hoặc 1 Americano cỡ lớn + 1 trà đá) được thêm 1 shot miễn phí vào 1 ly. Đơn từ 5.000 KRW qua Bombom Order cũng được thêm 1 shot miễn phí.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '카페 봄봄' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '타피오카 펄 추가 무료', 'Get free tapioca pearls with any drink order. Foam and other toppings are excluded.
For jumbo drinks, the amount of pearls provided will be the same as for a large size.
*Cannot be combined with other promotions, including Gong Cha Day or mobile carrier promotions offering greater discounts.
*The benefit also applies to accompanying guests. Student ID verification is required.', 'すべてのドリンク注文時、タピオカパールを無料追加。フォームやその他のトッピングは対象外。
ジャンボサイズには、ラージサイズ相当量のパールを提供。
*ゴンチャデーや通信会社の割引率が高いキャンペーンなど、他の特典との併用不可。
*同伴者にも適用可能。学生証の提示必須。', 'Khi gọi bất kỳ đồ uống nào, được thêm trân châu tapioca miễn phí. Không áp dụng cho lớp foam hoặc các loại topping khác.
Với size Jumbo, lượng trân châu được tặng tương đương size Large.
*Không áp dụng đồng thời với ưu đãi khác, bao gồm Gong Cha Day hoặc chương trình giảm giá cao của nhà mạng.
*Ưu đãi áp dụng cho cả người đi cùng. Bắt buộc xuất trình thẻ sinh viên.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '공차' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '음료 2잔 주문 시 1샷 추가 (월요일)', 'Get 1 extra shot when ordering 2 drinks.
*Available on Mondays only. Student ID required.', 'ドリンク2杯の注文で、エスプレッソショット1杯を無料追加。
*毎週月曜日のみ。学生証の提示必須。', 'Khi gọi 2 đồ uống, được thêm miễn phí 1 shot espresso.
*Chỉ áp dụng vào thứ Hai hằng tuần. Bắt buộc xuất trình thẻ sinh viên.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '빽다방' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '계좌이체 or 현금 1만원 충전 시 10시간 + 음료 제공', 'Pay 10,000 KRW by bank transfer or cash to receive 10 hours plus 1 free drink.', '現金または口座振込で10,000ウォンをチャージすると、10時間＋ドリンク1杯を提供。', 'Nạp 10.000 KRW bằng tiền mặt hoặc chuyển khoản được tặng 10 giờ sử dụng và 1 đồ uống.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '에이플 PC방' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '게임 이용료 15% 할인', 'Get 15% off game fees.', 'ゲーム利用料15%割引。', 'Giảm 15% phí chơi game.', '2026-09-11', '2026-12-21', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '히어로 보드게임 카페' AND c.name_ko = '경제통상대학';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '모든 음료 15% 할인', '15% off all drinks.', 'すべてのドリンクが15%割引。', 'Giảm 15% cho tất cả đồ uống.', '2026-09-01', '2026-12-31', 'NORMAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '산1-1' AND c.name_ko = '총학생회';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '음료 및 주류 2+1 (음료/주류 간 교차 불가) & 테이블당 신메뉴 ''찰도그'' 오리지널, 체다치즈 중 택1 제공', 'Buy 2, get 1 free on soft drinks or alcoholic beverages. 
Items cannot be mixed across the two categories. 
Each table also receives 1 complimentary Chaldog, with a choice of Original or Cheddar Cheese.', 'ソフトドリンクまたは酒類を2つ購入すると1つ無料。
ソフトドリンクと酒類の組み合わせは不可。
さらに、1テーブルにつき新メニュー「チャルドッグ」のオリジナルまたはチェダーチーズのうち1つを無料提供。', 'Mua 2 tặng 1 đối với nước ngọt hoặc đồ uống có cồn. 
Không được kết hợp hai loại với nhau. 
Mỗi bàn còn được tặng 1 Chaldog, chọn vị Original hoặc Cheddar Cheese.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '교촌치킨' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만 원 이하 주문 시 음료 제공 / 5만 원 이상 주문 시 주류 제공 / 10만 원 이상 주문 시 10% 할인', 'Orders of 50,000 KRW or less include 1 free soft drink. 
Orders of 50,000 KRW or more include 1 free alcoholic drink. 
Orders of 100,000 KRW or more receive 10% off.', '50,000ウォン以下の注文でソフトドリンク1杯を無料提供。
50,000ウォン以上の注文でアルコール飲料1本を無料提供。
100,000ウォン以上の注文で10%割引。', 'Đơn từ 50.000 KRW trở xuống được tặng 1 đồ uống không cồn. 
Đơn từ 50.000 KRW trở lên được tặng 1 đồ uống có cồn. 
Đơn từ 100.000 KRW trở lên được giảm 10%.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '논두렁갈비' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '방문 식사 시 전 메뉴 10% 할인', 'Get 10% off all menu items when dining in.', '店内飲食時、全メニュー10%割引。', 'Giảm 10% toàn bộ menu khi ăn tại quán.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '다성반점' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만원 이상 결제 시, 5% 할인 or 소주 2병 무료 (현금, 카드결제 모두 가능)', 'For payments of 50,000 KRW or more, choose either 5% off or 2 free bottles of soju. 
Both cash and card payments are accepted.', '50,000ウォン以上の支払いで、5%割引または焼酎2本無料のうち1つを選択。
現金・カードのどちらでも決済可能。', 'Thanh toán từ 50.000 KRW trở lên được chọn giảm 5% hoặc tặng miễn phí 2 chai soju. 
Áp dụng cho cả thanh toán bằng tiền mặt và thẻ.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '먹돼지' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '음료수 1캔 제공 & 영수증 리뷰 시 미나리 제공', 'Get 1 free canned soft drink. 
Leave a receipt review to receive complimentary minari.', '缶ドリンク1本を無料提供。
レシートレビューを投稿すると、セリを無料提供。', 'Được tặng 1 lon nước ngọt. 
Khi viết đánh giá bằng hóa đơn, được tặng thêm rau minari.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도동 솥뚜껑' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4만 원 이상 주문 시 소주 1병 제공', 'Get 1 free canned soft drink, plus complimentary minari when you leave a receipt review.', '40,000ウォン以上の注文で焼酎1本を無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng 1 chai soju.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '숯가마 바베큐 치킨' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '6만 원 이상 주문 시 치즈볼 제공', 'Get free cheese balls with orders of 60,000 KRW or more.', '60,000ウォン以上の注文でチーズボールを無料提供。', 'Đơn từ 60.000 KRW trở lên được tặng bánh viên phô mai.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '리얼후라이' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만원 이상 구매 시 음료수 2캔 or 소주 혹은 맥주 한병 제공', 'For purchases of 50,000 KRW or more, choose either 2 canned soft drinks, 1 bottle of soju, or 1 bottle of beer.', '50,000ウォン以上の購入で、缶ドリンク2本、焼酎1本、またはビール1本のうち1つを無料提供。', 'Mua từ 50.000 KRW trở lên được chọn miễn phí 1 trong 3: 2 lon nước ngọt, 1 chai soju hoặc 1 chai bia.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '사랑과 평화' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '안주 주문 시 음료 1캔 무료', 'Get 1 free canned soft drink when ordering any bar food item.', 'おつまみを注文すると、缶ドリンク1本を無料提供。', 'Khi gọi món nhắm, được tặng 1 lon nước ngọt.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '가치' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만 원 이상 주문 시 사이드 제공', 'Get 1 free side menu item with orders of 50,000 KRW or more.', '50,000ウォン以上の注文で、サイドメニュー1品を無料提供。', 'Đơn từ 50.000 KRW trở lên được tặng 1 món phụ.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '파동추야' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만 원 이상 주문 시 음료 1캔 제공', 'Get 1 free canned soft drink with orders of 50,000 KRW or more.', '50,000ウォン以上の注文で、缶ドリンク1本を無料提供。', 'Đơn từ 50.000 KRW trở lên được tặng 1 lon nước ngọt.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '짚동가리쌩주' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만 원 이상 주문 시 소주 1병 제공', 'Get 1 free bottle of soju with orders of 50,000 KRW or more.', '50,000ウォン以上の注文で焼酎1本を無料提供。', 'Đơn từ 50.000 KRW trở lên được tặng 1 chai soju.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '젠사이야' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만 원 이상 주문 시 음료 1캔 제공', 'Get 1 free canned soft drink with orders of 50,000 KRW or more.', '50,000ウォン以上の注文で、缶ドリンク1本を無料提供。', 'Đơn từ 50.000 KRW trở lên được tặng 1 lon nước ngọt.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '블루힐' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만 원 이상 구매 시 5% 할인 (계좌이체 시 & 이용시간 2시간 제한)', 'Get 5% off purchases of 50,000 KRW or more when paying by bank transfer. 
Usage is limited to 2 hours.', '50,000ウォン以上の購入を口座振込で支払うと、5%割引。
利用時間は2時間まで。', 'Mua từ 50.000 KRW trở lên và thanh toán bằng chuyển khoản được giảm 5%. 
Thời gian sử dụng giới hạn trong 2 giờ.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '인쌩맥주' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '4만 원 이상 주문 시 라면땅 제공', 'Get a free ramyeon snack with orders of 40,000 KRW or more.', '40,000ウォン以上の注文で、ラーメンスナックを無料提供。', 'Đơn từ 40.000 KRW trở lên được tặng snack mì ramyeon.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '엉클호프' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '5만 원 이상 주문 시 음료 1캔 제공', 'Get 1 free canned soft drink with orders of 50,000 KRW or more.', '50,000ウォン以上の注文で、缶ドリンク1本を無料提供。', 'Đơn từ 50.000 KRW trở lên được tặng 1 lon nước ngọt.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '역전할머니맥주' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '현금 또는 계좌 이체 시 5% 할인', 'Get 5% off when paying by cash or bank transfer.', '現金または口座振込で支払うと、5%割引。', 'Giảm 5% khi thanh toán bằng tiền mặt hoặc chuyển khoản.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '상도로 3가' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '2만 원 이상 주문 시 팝콘 제공', 'Get free popcorn with orders of 20,000 KRW or more.', '20,000ウォン以上の注文でポップコーンを無料提供。', 'Đơn từ 20.000 KRW trở lên được tặng bỏng ngô.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '씨밤' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '1만 8천 원 이상 주문 시 쉘, 시리얼, 샤인머스캣 중 택1 제공 (*현금 또는 계좌 이체만 가능)', 'For orders of 18,000 KRW or more, choose one complimentary option: a shell topping, cereal, or Shine Muscat grapes. 
Cash or bank transfer only.', '18,000ウォン以上の注文で、シェルトッピング、シリアル、またはシャインマスカットのうち1つを無料提供。
現金または口座振込のみ対象。', 'Đơn từ 18.000 KRW trở lên được chọn miễn phí 1 trong 3: lớp phủ shell, ngũ cốc hoặc nho Shine Muscat. 
Chỉ áp dụng khi thanh toán bằng tiền mặt hoặc chuyển khoản.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '요아정' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '빅사이즈 2잔 주문 시 샷 추가 제공', 'Get 1 free extra shot when ordering 2 large drinks.', 'ビッグサイズのドリンクを2杯注文すると、ショット1回を無料追加。', 'Gọi 2 đồ uống cỡ lớn được thêm miễn phí 1 shot.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '카페 봄봄' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '세트 선택 시 최대 5천 원 음료 선택 가능', 'When ordering a set, choose a drink priced up to 5,000 KRW.', 'セット注文時、5,000ウォン以下のドリンクを選択可能。', 'Khi chọn set menu, có thể chọn đồ uống có giá tối đa 5.000 KRW.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '놀숲' AND c.name_ko = '축제';
INSERT INTO partnership (description, description_en, description_ja, description_vi, start_date, end_date, period_type, partnership_restaurant_id, partnership_college_id, partnership_department_id)
SELECT '이용료 15% 할인', 'Get 15% off usage fees.', '利用料金15%割引。', 'Giảm 15% phí sử dụng.', '2026-09-15', '2026-09-16', 'FESTIVAL', pr.partnership_restaurant_id, c.college_id, NULL
FROM partnership_restaurant pr, college c
WHERE pr.store_name_ko = '히어로 보드게임 카페' AND c.name_ko = '축제';
