---
paths:
  - "src/main/java/ssu/eatssu/domain/admin/**/*.java"
---

# Admin Domain Notes

- admin은 별도 인증 체계가 아니라 일반 API와 동일한 JWT(`ROLE_ADMIN`)를 사용한다. 서버 렌더링 UI가 아니라 다른 도메인과 동일한 `@ResponseBody` JSON API다.
- admin 패키지는 다른 도메인(`review`, `menu` 등)의 엔티티를 그 도메인의 서비스/리포지토리를 거치지 않고 `domain/admin/persistence`의 자체 리포지토리로 직접 조회/수정한다. 이는 이 도메인의 의도된 컨벤션이며, 새 admin 기능을 추가할 때도 이 패턴을 따른다.
- 리뷰 삭제 시 연관 리포트 삭제는 `ApplicationEventPublisher`로 발행되는 `ReviewDeleteEvent`를 `ReportEventListener`가 동기적으로(`@TransactionalEventListener`가 아닌 일반 `@EventListener`) 처리한다.
- `ManageInquiryService`는 `inquiry` 도메인의 `InquiryStatus` 전이를 직접 처리한다. 자세한 내용은 [[inquiry]] 참고.
