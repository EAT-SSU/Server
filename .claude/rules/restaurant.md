---
paths:
  - "src/main/java/ssu/eatssu/domain/restaurant/**/*.java"
---

# Restaurant Domain Notes

- 이 패키지는 자체 엔티티/테이블이 없고 `Restaurant`(캠퍼스 식당 enum), `RestaurantType`(FIXED/VARIABLE 분류 enum) 두 개로만 구성된다. `menu` 도메인의 하위 개념에 가깝다.
- `partnership` 패키지에도 이름이 완전히 같은 `RestaurantType` enum이 따로 있다(`restaurant`는 FIXED/VARIABLE, `partnership`은 RESTAURANT/CAFE/PUB로 의미가 전혀 다름). import할 때 IDE 자동완성이 잘못된 쪽을 잡아줄 수 있으니 패키지 경로를 반드시 확인한다.
