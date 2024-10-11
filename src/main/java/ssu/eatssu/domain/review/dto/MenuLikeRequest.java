package ssu.eatssu.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.entity.Review;

@Getter
@AllArgsConstructor
public class MenuLikeRequest {
    private Long menuId;
    private Boolean like;

//    public MenuReview toMenuReviewEntity(Review review, Menu menu) {
//        return MenuReview.builder()
//                .review(review)
//                .menu(menu)
//                .like(like)
//                .build();
//    }
}
