---
paths:
  - "src/main/java/ssu/eatssu/domain/menu/**/*.java"
---

# Menu Domain Notes

- `Menu`에는 이름이 있고 `Meal`에는 없다. VARIABLE 타입(`Menu.createVariable`) 메뉴는 `(name, restaurant)` 기준으로 get-or-create되어 여러 `Meal`에서 `MealMenu`를 통해 재사용된다 — Meal마다 새로 생성되는 것이 아니다.
- i18n 컬럼(`name_en`/`name_ja`/`name_vi`)은 `Menu`에만 있고 `Meal`/`MealMenu`/`MenuCategory`에는 없다. 그마저도 SNACK_CORNER 메뉴만 채워져 있고 FOOD_COURT나 VARIABLE로 생성된 메뉴는 비어 있다. `MealController`는 Language를 아예 받지 않아 변동 식단 메뉴명은 항상 한국어로 나간다.
- `Restaurant.FACULTY`는 `MenuType`/`RestaurantType`의 FIXED/VARIABLE 어느 분류에도 속하지 않는다. 검증 로직(`validateMenuRestaurant`/`validateMealRestaurant`)이 화이트리스트가 아니라 블랙리스트 방식이라 새 restaurant enum 값을 추가할 때 분류 누락에 주의해야 한다.
- Meal 재조회/중복 방지(`getExistingMealId`)는 같은 date/timePart/restaurant에서 메뉴 이름 리스트를 정렬해 문자열로 정확히 비교한다. 공백/오탈자 하나만 달라도 별도 Meal로 새로 생성된다.
- `menu/persistence`의 `Querydsl*Rating*` 클래스들은 `rating` 도메인의 별점 집계를 실제로 구현한다. 자세한 내용은 [[rating]] 참고.
