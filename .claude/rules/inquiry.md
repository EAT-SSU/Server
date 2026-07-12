---
paths:
  - "src/main/java/ssu/eatssu/domain/inquiry/**/*.java"
  - "src/main/java/ssu/eatssu/domain/admin/service/ManageInquiryService.java"
---

# Inquiry Domain Notes

- `InquiryController`(문의 작성 API)는 `@Deprecated`다. 문의 기능이 카카오톡으로 이전되어 더는 사용되지 않지만 코드는 아직 남아 동작한다. 새 기능을 여기에 추가하지 않는다.
- 문의 상태(`InquiryStatus`: WAITING/ANSWERED/HOLD) 전이는 `inquiry` 패키지가 아니라 `domain/admin`(`ManageInquiryService`)에서 처리한다. `InquiryStatus`를 변경하면 admin 패키지도 함께 확인해야 한다. `ManageInquiryService`가 다른 위치로 옮겨지면 위 `paths`도 함께 수정할 것.
