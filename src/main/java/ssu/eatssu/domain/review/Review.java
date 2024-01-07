package ssu.eatssu.domain.review;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import ssu.eatssu.domain.rate.Rate;
import ssu.eatssu.domain.user.BaseTimeEntity;
import ssu.eatssu.domain.menu.Menu;
import ssu.eatssu.domain.user.User;
import ssu.eatssu.web.review.dto.UpdateReviewRequest;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    private String content;

    @Embedded
    private Rate rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImage> reviewImages;

    public void update(UpdateReviewRequest request) {
        this.content = request.getContent();
        this.rate = Rate.of(request.getMainRate(), request.getAmountRate(), request.getTasteRate());
    }

    public boolean isDifferentUser(User user) {
        return !this.user.equals(user);
    }

    public void clearUser() {
        this.user = null;
    }
}
