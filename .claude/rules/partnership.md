---
paths:
  - "src/main/java/ssu/eatssu/domain/partnership/**/*.java"
---

# Partnership Domain Notes

- `PartnershipRestaurant`(가게, 부모)와 `Partnership`(개별 혜택, 자식)은 1:N 관계다. `PartnershipLike`는 개별 혜택이 아니라 가게 단위로 붙는다.
- `Partnership` 엔티티에 `@Where(clause = "end_date >= CURRENT_DATE")`가 걸려 있어 `findById`를 포함한 모든 JPA 조회가 만료된 혜택을 자동으로 숨긴다. 만료된 partnership을 `findById`로 찾으면 "만료됨"이 아니라 "찾을 수 없음"으로 처리된다는 점에 주의한다.
- `PeriodType.FESTIVAL`은 `CreatePartnershipRequest`에 필드가 없어 API로는 생성할 수 없다. FESTIVAL 혜택은 현재 DB를 직접 조작해야만 만들 수 있다.
- 앱 API로 생성한 `Partnership`/`PartnershipRestaurant`는 i18n 필드(`description_en/ja/vi`, `store_name_en/ja/vi`)가 채워지지 않는다. 지금까지 이 필드들은 전부 별도 flyway 마이그레이션으로만 채워졌다.
- 좋아요 수(`likeCount`)는 원자적 카운터가 아니라 로드된 컬렉션의 `size()`로 계산되고, 좋아요 토글도 잠금 없는 read-check-write 방식이다. 동시 요청에 대한 정합성을 기대하지 않는다.
