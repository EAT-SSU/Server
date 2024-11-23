package ssu.eatssu.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.user.entity.BaseTimeEntity;
import ssu.eatssu.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    private String content;

    // TODO : 삭제되어야 함
    @Embedded
    private Ratings ratings;

    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // TODO : 삭제되어야 함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewMenuLike> reviewMenuLikes = new ArrayList<>();

    public void update(String content, Integer mainRate, Integer amountRate, Integer tasteRate) {
        this.content = content;
        this.ratings = Ratings.of(mainRate, amountRate, tasteRate);
    }

    public boolean isNotWrittenBy(User user) {
        return !this.user.equals(user);
    }

    public void clearUser() {
        this.user = null;
    }

    public void addReviewImage(String imageUrl) {
        ReviewImage reviewImage = new ReviewImage(this, imageUrl);
        this.reviewImages.add(reviewImage);
    }

    public void addReviewMenuLike(Menu menu, boolean isLike) {
        ReviewMenuLike reviewMenuLike = ReviewMenuLike.create(this, menu, isLike);
        this.reviewMenuLikes.add(reviewMenuLike);

        if (isLike) {
            menu.increaseLikeCount();
        } else {
            menu.increaseUnlikeCount();
        }
    }

    public void removeReviewMenuLike(ReviewMenuLike reviewMenuLike) {
        this.reviewMenuLikes.remove(reviewMenuLike);

        if (reviewMenuLike.getIsLike()) {
            reviewMenuLike.getMenu().decreaseLikeCount();
        } else {
            reviewMenuLike.getMenu().decreaseUnlikeCount();
        }
    }
}
