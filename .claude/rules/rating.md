---
paths:
  - "src/main/java/ssu/eatssu/domain/rating/**/*.java"
---

# Rating Domain Notes

- 별점 집계는 매 조회마다 DB에서 `AVG()`/`COUNT()`를 다시 계산하는 방식이다(캐시나 비정규화된 집계 필드 없음). `QuerydslMealRatingCalculator`/`QuerydslMenuRatingCalculator` 등 `menu/persistence` 쪽 구현체가 실제 사용 경로다.
- `rating/entity`의 `JpaLoadCollectionRatingCalculator`, `JpaProjectionRatingCalculator`, `ReviewRating`, `RatingCountMap`은 벤치마크용으로 만들어진 미사용 코드다. 특히 `ReviewRating`은 enum을 mutable singleton으로 사용해 스레드 세이프하지 않으니, 되살려서 쓰지 말 것.
- `amountRating`/`tasteRating`은 값이 있어도 응답 DTO에서 세팅되지 않아 항상 null로 내려간다. `Ratings.java`의 FIXME(2026-07-04)에 이 필드들의 필수 여부가 아직 비즈니스적으로 미정임이 명시되어 있으니, 고칠 때 비즈니스 요구사항부터 확인한다.
