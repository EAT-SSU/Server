package ssu.eatssu.domain.inquiry.entity;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.user.entity.BaseTimeEntity;
import ssu.eatssu.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Inquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_inquiry_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String email;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    public Inquiry(String content, User user, String email) {
        this.content = content;
        this.user = user;
        this.email = email;
        this.status = InquiryStatus.WAITING;
    }

    public void updateStatus(InquiryStatus status) {
        this.status = status;
    }

}
