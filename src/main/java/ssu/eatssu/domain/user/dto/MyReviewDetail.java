package ssu.eatssu.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.review.entity.Review;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@Schema(title = "리뷰 상세 - 내 리뷰 리스트 조회 용")
@Getter
public class MyReviewDetail {

    @Schema(description = "리뷰 식별자", example = "123")
    Long reviewId;

    @Schema(description = "평점-메인", example = "4")
    private Integer mainRating;

    @Schema(description = "평점-양", example = "4")
    private Integer amountRating;

    @Schema(description = "평점-맛", example = "4")
    private Integer tasteRating;

    @Schema(description = "리뷰 작성 날짜(format = yyyyMMdd)", example = "20230407")
    private LocalDate writeDate;

    @Schema(description = "메뉴 이름", example = "돈까스")
    private String menuName;

    @Schema(description = "리뷰 내용", example = "맛있습니당")
    private String content;

    @Schema(description = "리뷰 이미지 url 리스트", example = "[\"imgurl1\", \"imgurl2\"]")
    private List<String> imgUrlList;

    public static MyReviewDetail from(Review review) {
        List<String> imgUrlList = new ArrayList<>();
        if (review.getReviewImages() != null) {
            review.getReviewImages().forEach(image -> {
                if (image != null && image.getImageUrl() != null) {
                    imgUrlList.add(image.getImageUrl());
                }
            });
        }

        Ratings ratings = review.getRatings();
        int mainRating = 0;

        if (ratings != null) {
            mainRating = ratings.getMainRating() != null ? ratings.getMainRating() : 0;
        }

        String menuName = review.getMenu() != null ? review.getMenu().getName() : null;
        LocalDate writeDate = review.getCreatedDate() != null ? review.getCreatedDate().toLocalDate() : null;

        return MyReviewDetail.builder()
                             .reviewId(review.getId())
                             .mainRating(mainRating)
                             .writeDate(writeDate)
                             .content(review.getContent())
                             .imgUrlList(imgUrlList)
                             .menuName(menuName)
                             .build();
    }

}
