package ssu.eatssu.domain.review.dto;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.review.dto.request.MenuLikeRequest;
import ssu.eatssu.domain.review.dto.request.ReviewUpdateRequest;
import ssu.eatssu.domain.review.dto.request.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.dto.response.MenuIdNameDto;
import ssu.eatssu.domain.review.dto.response.MenuIdNameLikeDto;
import ssu.eatssu.domain.review.dto.response.ReviewTranslationResponse;
import ssu.eatssu.domain.review.dto.response.SavedReviewImage;
import ssu.eatssu.domain.review.dto.response.ValidMenuForViewResponse;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.review.entity.Review;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ReviewDtoCoverageTest {

    @Test
    void mapsSmallRequestAndResponseDtos() {
        MenuLikeRequest like = new MenuLikeRequest(3L, true);
        UpdateMealReviewRequest update = new UpdateMealReviewRequest(4, List.of(like), "수정");
        ReviewUpdateRequest record = new ReviewUpdateRequest(5, 4, 3, "기록");
        ReviewTranslationResponse translation = new ReviewTranslationResponse(1L, Language.EN, "translated", true);
        SavedReviewImage image = new SavedReviewImage("image.jpg");
        MenuIdNameDto menu = new MenuIdNameDto(2L, "돈가스");
        MenuIdNameLikeDto liked = new MenuIdNameLikeDto(2L, "돈가스", true);
        ValidMenuForViewResponse valid = ValidMenuForViewResponse.builder()
                .menuList(List.of(ValidMenuForViewResponse.MenuDto.builder().menuId(2L).name("돈가스").build()))
                .build();

        assertThat(like.getMenuId()).isEqualTo(3L);
        assertThat(like.getIsLike()).isTrue();
        assertThat(update.getRating()).isEqualTo(4);
        assertThat(update.getMenuLikes()).containsExactly(like);
        assertThat(record.content()).isEqualTo("기록");
        assertThat(translation.cached()).isTrue();
        assertThat(image.getUrl()).isEqualTo("image.jpg");
        assertThat(menu.name()).isEqualTo("돈가스");
        assertThat(liked.isLike()).isTrue();
        assertThat(valid.getMenuList()).hasSize(1);
    }

    @Test
    void mapsRatingWrappers() {
        Ratings ratings = Ratings.of(4, 3, 2);
        RatingsDto dto = new RatingsDto(ratings);
        RatingAverages averages = RatingAverages.builder().mainRating(4.25).build();

        assertThat(dto.getMainRating()).isEqualTo(4);
        assertThat(averages.mainRating()).isEqualTo(4.25);
        assertThat(ReviewRatingCount.from(List.of(Review.builder().ratings(ratings).build())))
                .isEqualTo(new ReviewRatingCount(0, 0, 0, 1, 0));
    }
}
