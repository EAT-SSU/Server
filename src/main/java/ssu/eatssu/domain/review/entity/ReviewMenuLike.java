package ssu.eatssu.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.menu.entity.Menu;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ReviewMenuLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_menu_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Column(nullable = false)
    private Boolean isLike; // true : 좋아요, false : 싫어요

    public static ReviewMenuLike create(Review review, Menu menu, Boolean isLike) {
        return ReviewMenuLike.builder()
                .review(review)
                .menu(menu)
                .isLike(isLike)
                .build();
    }

    public void updateLike(Boolean like) {
        isLike = like;
    }

    public void resetMenuLikeStatus() {
        menu.cancelLike(this.isLike);
    }
}
