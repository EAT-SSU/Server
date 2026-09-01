package ssu.eatssu.domain.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewImage;
import ssu.eatssu.domain.user.dto.request.UpdateDepartmentRequest;
import ssu.eatssu.domain.user.dto.response.GetCollegeResponse;
import ssu.eatssu.domain.user.dto.response.GetDepartmentResponse;
import ssu.eatssu.domain.user.dto.response.MyMealReviewResponse;
import ssu.eatssu.domain.user.dto.response.MyReviewDetail;
import ssu.eatssu.domain.user.entity.Language;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class UserDtoCoverageTest {

    @Test
    void mapsDepartmentRequestAndResponseRecords() {
        UpdateDepartmentRequest request = new UpdateDepartmentRequest(4L);
        UpdateDepartmentRequest empty = new UpdateDepartmentRequest();
        GetCollegeResponse college = GetCollegeResponse.builder().id(1L).name("공과대학").build();
        GetDepartmentResponse department = GetDepartmentResponse.builder().id(2L).name("컴퓨터학부").build();

        assertThat(request.getDepartmentId()).isEqualTo(4L);
        assertThat(empty.getDepartmentId()).isNull();
        assertThat(college.id()).isEqualTo(1L);
        assertThat(college.name()).isEqualTo("공과대학");
        assertThat(department.id()).isEqualTo(2L);
        assertThat(department.name()).isEqualTo("컴퓨터학부");
    }

    @Test
    void mapsMyReviewDetailIncludingNullSafeFields() {
        Menu menu = mock(Menu.class);
        given(menu.getName()).willReturn("돈가스");
        Review review = Review.builder().id(9L).menu(menu).ratings(Ratings.of(5, 4, 3)).content("맛있어요")
                .reviewImages(List.of(new ReviewImage(null, "image.jpg"))).build();
        ReflectionTestUtils.setField(review, "createdDate", LocalDateTime.of(2026, 9, 1, 10, 0));

        MyReviewDetail response = MyReviewDetail.from(review);

        assertThat(response.getReviewId()).isEqualTo(9L);
        assertThat(response.getMainRating()).isEqualTo(5);
        assertThat(response.getWriteDate()).isEqualTo(LocalDate.of(2026, 9, 1));
        assertThat(response.getMenuName()).isEqualTo("돈가스");
        assertThat(response.getImgUrlList()).containsExactly("image.jpg");

        Review empty = Review.builder().build();
        assertThat(MyReviewDetail.from(empty).getMainRating()).isZero();
        assertThat(MyReviewDetail.from(empty).getWriteDate()).isNull();
    }

    @Test
    void myReviewDetailHandlesNullImageList() {
        Review review = mock(Review.class);
        given(review.getReviewImages()).willReturn(null);

        MyReviewDetail response = MyReviewDetail.from(review);

        assertThat(response.getImgUrlList()).isEmpty();
    }

    @Test
    void myReviewDetailSkipsImagesWithoutUrl() {
        ReviewImage blankImage = mock(ReviewImage.class);
        given(blankImage.getImageUrl()).willReturn(null);
        Review review = Review.builder().reviewImages(java.util.Arrays.asList(blankImage, null)).build();

        MyReviewDetail response = MyReviewDetail.from(review);

        assertThat(response.getImgUrlList()).isEmpty();
    }

    @Test
    void mapsMyMealReviewForMealAndLegacyMenuReview() {
        Menu included = mock(Menu.class);
        given(included.getId()).willReturn(1L);
        given(included.getName()).willReturn("돈가스");
        Meal meal = mock(Meal.class);
        given(meal.getMenus()).willReturn(List.of(included));
        Review mealReview = Review.builder().id(3L).meal(meal).rating(4).reviewImages(List.of()).menuLikes(List.of())
                .content("좋아요").build();
        ReflectionTestUtils.setField(mealReview, "createdDate", LocalDateTime.of(2026, 9, 1, 11, 0));

        MyMealReviewResponse mealResponse = MyMealReviewResponse.from(mealReview);

        assertThat(mealResponse.getRating()).isEqualTo(4);
        assertThat(mealResponse.getMenuList()).extracting("name").containsExactly("돈가스");

        Menu menu = mock(Menu.class);
        given(menu.getId()).willReturn(2L);
        given(menu.getName()).willReturn("샐러드");
        Review menuReview = Review.builder().menu(menu).ratings(Ratings.of(5, null, null)).reviewImages(List.of())
                .menuLikes(List.of()).build();
        ReflectionTestUtils.setField(menuReview, "createdDate", LocalDateTime.of(2026, 9, 1, 11, 0));

        assertThat(MyMealReviewResponse.from(menuReview).getRating()).isEqualTo(5);
        assertThat(MyMealReviewResponse.from(menuReview).getMenuList().get(0).name()).isEqualTo("샐러드");
    }

    @Test
    void myMealReviewFallsBackToLegacyRatingWhenMainRatingIsMissing() {
        Menu menu = mock(Menu.class);
        given(menu.getId()).willReturn(2L);
        given(menu.getName()).willReturn("샐러드");
        Ratings ratingsWithoutMain = mock(Ratings.class);
        given(ratingsWithoutMain.getMainRating()).willReturn(null);
        Review review = Review.builder().menu(menu).ratings(ratingsWithoutMain).rating(3)
                .reviewImages(List.of()).menuLikes(List.of()).build();
        ReflectionTestUtils.setField(review, "createdDate", LocalDateTime.of(2026, 9, 1, 11, 0));

        assertThat(MyMealReviewResponse.from(review).getRating()).isEqualTo(3);
    }

    @Test
    void myMealReviewExcludesFilteredMenuNamesFromMealMenuList() {
        Menu excluded = mock(Menu.class);
        given(excluded.getId()).willReturn(1L);
        given(excluded.getName()).willReturn("쌀밥");
        Meal meal = mock(Meal.class);
        given(meal.getMenus()).willReturn(List.of(excluded));
        Review review = Review.builder().meal(meal).rating(4).reviewImages(List.of()).menuLikes(List.of()).build();
        ReflectionTestUtils.setField(review, "createdDate", LocalDateTime.of(2026, 9, 1, 11, 0));

        assertThat(MyMealReviewResponse.from(review).getMenuList()).isEmpty();
    }
}
