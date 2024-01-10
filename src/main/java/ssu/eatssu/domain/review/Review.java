package ssu.eatssu.domain.review;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.menu.Menu;
import ssu.eatssu.domain.rating.Ratings;
import ssu.eatssu.domain.user.BaseTimeEntity;
import ssu.eatssu.domain.user.User;

import java.util.List;


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
    private Ratings ratings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImage> reviewImages;

    //Entity -> Dto 방향 의존관계 제거
    public void update(String content, Integer mainRate, Integer amountRate, Integer tasteRate) {
        this.content = content;
        this.ratings = Ratings.of(mainRate, amountRate, tasteRate);
    }

    /*
    public void update(UpdateReviewRequest request) {
        this.content = request.getContent();
        this.ratings = Ratings.of(request.getMainRate(), request.getAmountRate(), request.getTasteRate());
    }
    */

    public boolean isDifferentUser(User user) {
        return !this.user.equals(user);
    }

    public void clearUser() {
        this.user = null;
    }

}
