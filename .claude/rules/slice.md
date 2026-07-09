---
paths:
  - "src/main/java/ssu/eatssu/domain/slice/**/*.java"
---

# Slice Domain Notes

- 커서는 별도 인코딩 없이 정렬 대상 PK(`review.id`) 값 그대로 쓴다. 커서 기반 페이지네이션을 다른 정렬 기준으로 확장하려면 이 "PK 그대로" 가정이 깨진다는 점을 먼저 고려한다.
- 컨트롤러의 `@PageableDefault(sort = "date")`는 실제로 무시된다. 리포지토리(QueryDSL)는 항상 `id` 기준으로 정렬하므로, 클라이언트가 sort 파라미터를 보내도 조용히 무시된다.
