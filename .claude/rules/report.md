---
paths:
  - "src/main/java/ssu/eatssu/domain/report/**/*.java"
---

# Report Domain Notes

- `Report` 엔티티는 `report` 패키지가 아니라 `review.entity.Report`에 있다. `ReportType`/`ReportStatus`/서비스/컨트롤러만 `report` 패키지에 있다.
- 신고 접수는 Slack 알림(`SlackChannel.REPORT_CHANNEL`)만 보낼 뿐, 리뷰를 자동으로 숨기거나 카운트를 올리는 등의 후속 처리가 전혀 없다. 실제 조치는 전부 Slack을 보고 admin이 수동으로 처리한다.
- `ReportStatus`는 `PENDING` 외 값(`IN_PROGRESS`/`RESOLVED`/`REJECTED`/`FALSE_REPORT`)으로 전이시키는 코드가 어디에도 없다. 상태 관리 기능을 새로 만들기 전까지는 사실상 `PENDING` 고정이라고 생각한다.
