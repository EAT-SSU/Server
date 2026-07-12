---
paths:
  - "src/main/java/ssu/eatssu/domain/review/**/*.java"
---

# Review Domain Notes

- 같은 `Review` 엔티티에 V1용 `@Embedded Ratings ratings`와 V2용 `Integer rating`이 함께 존재한다(엔티티 상단 주석 참고). 두 필드는 서로 배타적으로 채워질 수 있으니 어느 한쪽만 보고 값이 있다고 가정하면 안 된다.
- V1 리뷰는 항상 `review.menu`로 연결되고, V2 meal 리뷰는 `review.meal`을 직접 사용한다. V1 API(`GET /reviews`)는 `review.menu` 기준으로만 조회하므로 V2로 작성된 meal 리뷰는 V1에서 보이지 않는다.
- V2 집계(`ReviewServiceV2.findMenuReviews`/`findMealReviews`/`findRestaurantReviews`)는 리뷰 리스트를 전부 로드한 뒤 Java에서 평균/카운트를 계산한다. V1(`ReviewRatingService`)은 QueryDSL로 DB에서 집계한다. 새 집계 기능을 추가할 때 어느 패턴을 따를지 확인한다.
- 리뷰 이미지 S3 업로드는 V1 엔드포인트(`POST /reviews/upload/image`)에서만 처리한다. V2 리뷰 생성 API는 이미 업로드된 `imageUrls`만 받으므로, V2 클라이언트도 이미지 첨부 시 V1 업로드 API를 먼저 호출해야 한다.
- `Report` 엔티티는 `report` 패키지가 아니라 `review.entity.Report`에 있다. 리뷰 신고 관련 로직을 찾을 때 `report` 패키지만 보면 안 된다. 상태/전이 등 나머지 신고 로직은 [[report]] 참고.
- `ReviewRatingService`(`review/service`)는 `rating` 도메인의 `RatingCalculator`를 구현해 실제 별점 집계를 담당한다. 자세한 내용은 [[rating]] 참고.
