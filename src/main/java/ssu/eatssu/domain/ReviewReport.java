package ssu.eatssu.domain;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.enums.ReportStatus;
import ssu.eatssu.domain.enums.ReviewReportType;
import ssu.eatssu.domain.review.Review;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewReport extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_report_id")
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User user;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Enumerated(EnumType.STRING)
    private ReviewReportType reportType;

    private String content;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;
}
