package ssu.eatssu.domain.slack.entity;

import org.springframework.stereotype.Component;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.user.entity.User;

import java.text.MessageFormat;

@Component
public class SlackMessageFormat {

    private SlackMessageFormat(){}

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
                - 리뷰 내용: {6}
                - 리뷰 날짜: {7}
                *신고 INFO*
                - 신고사유: {8}
                - 신고 날짜: {9}
                ===================
                """
        );
        Object[] args = {reporter.getId(), reporter.getNickname(), review.getId(), review.getUser().getId()
                , review.getUser().getNickname(), review.getMenu().getName(), review.getContent(),
                review.getModifiedDate().toString(), report.getReportType().getDescription(), report.getCreatedDate()};
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
        Object[] args = {inquiry.getUser().getId(), inquiry.getUser().getNickname(),inquiry.getUser().getEmail()
                ,inquiry.getCreatedDate(), inquiry.getContent()};
        return messageFormat.format(args);
    }
}
