package ssu.eatssu.domain.slack.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.global.handler.response.BaseException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class SlackMessageFormat {

    private static final long LOG_LINK_WINDOW_MILLIS = 5 * 60 * 1000;

    private static String serverEnv;
    private static String grafanaBaseUrl;
    private static String lokiDatasourceUid;
    private static ObjectMapper objectMapper;

    private SlackMessageFormat(@Value("${server.env:unknown}") String serverEnvValue,
                                @Value("${grafana.base-url:}") String grafanaBaseUrlValue,
                                @Value("${grafana.loki-datasource-uid:}") String lokiDatasourceUidValue,
                                ObjectMapper objectMapperValue) {
        SlackMessageFormat.serverEnv = serverEnvValue;
        SlackMessageFormat.grafanaBaseUrl = grafanaBaseUrlValue;
        SlackMessageFormat.lokiDatasourceUid = lokiDatasourceUidValue;
        SlackMessageFormat.objectMapper = objectMapperValue;
    }

    public static String sendReport(Report report) {
        User reporter = report.getUser();
        Review review = report.getReview();
        MessageFormat messageFormat = new MessageFormat(
                """
                        ===================
                        *신고자 INFO*
                        - 신고자 ID: {0}
                        - 닉네임: {1}
                        *신고된 리뷰 INFO*
                        - 리뷰 ID: {2}
                        - 리뷰 작성자 ID : {3}
                        - 리뷰 작성자 닉네임 : {4}
                        - 리뷰 메뉴: {5}
                        - 리뷰 식단 ID: {6}
                        - 리뷰 내용: {7}
                        - 리뷰 날짜: {8}
                        *신고 INFO*
                        - 신고사유: {9}
                        - 신고내용: {10}
                        - 신고 날짜: {11}
                        ===================
                        """
        );

        String menuName = review.getMenu() != null ? review.getMenu().getName() : "";
        String mealId = review.getMeal() != null ? String.valueOf(review.getMeal().getId()) : "";

        Object[] args = {reporter.getId(), reporter.getNickname(), review.getId(), review.getUser().getId(),
                review.getUser().getNickname(), menuName, mealId, review.getContent(),
                review.getModifiedDate().toString(), report.getReportType().getDescription(), report.getContent(),
                report.getCreatedDate()};
        return messageFormat.format(args);
    }

    public static String sendUserInquiry(Inquiry inquiry) {
        MessageFormat messageFormat = new MessageFormat(
                """
                        ===================
                        *문의 INFO*
                        - 문의자 ID: {0}
                        - 닉네임: {1}
                        - 이메일: {2}
                        *문의 내용*
                        - Date: {3}
                        - Content: {4}
                        ===================
                        """
        );
        Object[] args = {inquiry.getUser().getId(), inquiry.getUser().getNickname(), inquiry.getUser().getEmail()
                , inquiry.getCreatedDate(), inquiry.getContent()};
        return messageFormat.format(args);
    }

    public static String sendServerError(Throwable ex, String method, String uri, String userId, String args) {
        final String messageTemplate = """
                        ===================
                        *서버 에러 발생*
                        - {0}: {1}
                        - 예외 메시지: {2}
                        - 개발환경: {3}
                        *요청 정보*
                        - HTTP Method: {4}
                        - URI: {5}
                        - User ID: {6}
                        - 로그 내용 (파라미터 정보 포함): {7}
                        ===================
                        """;
        MessageFormat messageFormat = new MessageFormat(messageTemplate);

        String errorTypeLabel;
        Object errorTypeValue;
        String errorMessage;

        if (ex instanceof BaseException baseException) {
            errorTypeLabel = "예외 상태코드";
            errorTypeValue = baseException.getStatus();
            errorMessage = baseException.getStatus().getMessage();
        } else {
            errorTypeLabel = "예외 타입";
            errorTypeValue = ex.getClass().getSimpleName();
            errorMessage = ex.getMessage() != null ? ex.getMessage() : "메시지 없음";
        }

        Object[] formatArgs = {
                errorTypeLabel,
                errorTypeValue,
                errorMessage,
                serverEnv,
                method,
                uri,
                userId,
                args != null && args.length() > 500 ? args.substring(0, 500) + "...(truncated)" : args
        };
        String message = messageFormat.format(formatArgs);

        String logLink = buildGrafanaLogLink();
        if (logLink != null) {
            message += "\n" + logLink;
        }
        return message;
    }

    private static String buildGrafanaLogLink() {
        String requestId = MDC.get("requestId");
        if (grafanaBaseUrl == null || grafanaBaseUrl.isBlank()
                || lokiDatasourceUid == null || lokiDatasourceUid.isBlank()
                || requestId == null || requestId.isBlank()) {
            return null;
        }

        String application = "eatssu-" + serverEnv;
        String logQl = "{application=\"" + application + "\"} |= \"reqId=" + requestId + "\"";
        long now = System.currentTimeMillis();

        Map<String, Object> query = new LinkedHashMap<>();
        query.put("refId", "A");
        query.put("expr", logQl);
        query.put("datasource", Map.of("type", "loki", "uid", lokiDatasourceUid));
        query.put("editorMode", "code");

        Map<String, Object> range = new LinkedHashMap<>();
        range.put("from", String.valueOf(now - LOG_LINK_WINDOW_MILLIS));
        range.put("to", String.valueOf(now + LOG_LINK_WINDOW_MILLIS));

        Map<String, Object> pane = new LinkedHashMap<>();
        pane.put("datasource", lokiDatasourceUid);
        pane.put("queries", List.of(query));
        pane.put("range", range);
        pane.put("compact", false);

        try {
            String panesJson = objectMapper.writeValueAsString(Map.of("wes", pane));
            String encodedPanes = URLEncoder.encode(panesJson, StandardCharsets.UTF_8);
            String url = grafanaBaseUrl + "/explore?schemaVersion=1&panes=" + encodedPanes;
            return "<" + url + "|Grafana에서 로그 보기>";
        } catch (Exception e) {
            return null;
        }
    }
}
