package ssu.eatssu.domain.review;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.user.BaseTimeEntity;
import ssu.eatssu.domain.user.User;
import ssu.eatssu.domain.enums.ReportStatus;
import ssu.eatssu.domain.enums.ReviewReportType;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewReport extends BaseTimeEntity {

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
    private ReviewReportType reviewReportType;

    private String content;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;
}
