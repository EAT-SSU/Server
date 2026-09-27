package ssu.eatssu.domain.notification.infrastructure;

import java.util.Map;

public record PushMessage(String title, String body, Map<String, String> data) {

}
