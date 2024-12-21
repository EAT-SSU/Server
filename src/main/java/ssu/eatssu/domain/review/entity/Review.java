package ssu.eatssu.domain.review.entity;

import jakarta.persistence.*;
import java.util.Map;
import java.util.stream.Collectors;
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
    private List<ReviewMenuLike> menuLikes = new ArrayList<>();

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
        this.menuLikes.add(reviewMenuLike);

        if (isLike) {
            menu.increaseLikeCount();
        } else {
            menu.increaseUnlikeCount();
        }
    }

    public void removeReviewMenuLike(ReviewMenuLike reviewMenuLike) {
        this.menuLikes.remove(reviewMenuLike);

        if (reviewMenuLike.getIsLike()) {
            reviewMenuLike.getMenu().decreaseLikeCount();
        } else {
            reviewMenuLike.getMenu().decreaseUnlikeCount();
        }
    }

    public void update(String content, int rating, Map<Menu, Boolean> updatedMenuLikes) {
        this.content = content;
        this.rating = rating;

        // 현재 menu like 상태를 menu를 기준으로 매핑
        Map<Menu, ReviewMenuLike> currentMenuLikes = this.menuLikes.stream()
                .collect(Collectors.toMap(ReviewMenuLike::getMenu, menuLike -> menuLike));

        for (Map.Entry<Menu, Boolean> entry : updatedMenuLikes.entrySet()) {
            Menu menu = entry.getKey();
            Boolean isLike = entry.getValue();

            ReviewMenuLike currentMenuLike = currentMenuLikes.get(menu);

            if (currentMenuLike == null) {
                // 새롭게 추가된 menu like
                this.addReviewMenuLike(menu, isLike);
            } else if (!currentMenuLike.getIsLike().equals(isLike)) {
                // 기존 menu like 수정
                currentMenuLike.updateLike(isLike);
                menu.changeLikeStatus(isLike);
            }
            // 수정 후 map에서 제거(나머지는 삭제 대상)
            currentMenuLikes.remove(menu);
        }

        // 리뷰 요청 데이터에 없는 menu 항목이므로 삭제
        for (ReviewMenuLike remainingMenuLike : currentMenuLikes.values()) {
            this.menuLikes.remove(remainingMenuLike);
            remainingMenuLike.getMenu().cancelLike(remainingMenuLike.getIsLike());
        }
    }

    public void resetMenuLikes() {
        for (ReviewMenuLike reviewMenuLike : this.menuLikes) {
            reviewMenuLike.resetMenuLikeStatus();
        }
        this.menuLikes.clear();
    }
}
