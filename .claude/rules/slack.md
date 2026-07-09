---
paths:
  - "src/main/java/ssu/eatssu/domain/slack/**/*.java"
---

# Slack Domain Notes

- 예외 필터링 없이 모든 예외(비즈니스 예외 포함)가 `ControllerLogAspect`를 통해 Slack 전송 대상이 된다. 실제 전송 여부는 `SlackErrorNotifier`가 `server.env == "prod"`인지로만 가른다.
- 전송은 동기 방식이며 큐잉/rate-limit/dedup이 없다. 장애로 예외가 몰리면 실패 요청마다 Slack API 호출 지연이 응답 시간에 그대로 추가된다.
