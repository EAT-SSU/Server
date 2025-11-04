package ssu.eatssu.domain.slack.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.global.handler.response.BaseException;

import java.text.MessageFormat;

@Component
public class SlackMessageFormat {

    private static String serverEnv;

    private SlackMessageFormat(@Value("${server.env:unknown}") String serverEnvValue) {
        SlackMessageFormat.serverEnv = serverEnvValue;
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
                        - 요청 파라미터: {7}
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
        return messageFormat.format(formatArgs);
    }
}
