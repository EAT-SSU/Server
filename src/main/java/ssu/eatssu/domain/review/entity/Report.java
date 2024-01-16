package ssu.eatssu.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.report.dto.CreateReportRequest;
import ssu.eatssu.domain.report.entity.ReportType;
import ssu.eatssu.domain.user.entity.BaseTimeEntity;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.report.entity.ReportStatus;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    private String content;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    public static Report create(User user, Review review, CreateReportRequest request,
        ReportStatus status) {
        return Report.builder()
            .user(user)
            .review(review)
            .reportType(request.getReportType())
            .content(request.getContent())
            .status(status)
            .build();
    }
}
