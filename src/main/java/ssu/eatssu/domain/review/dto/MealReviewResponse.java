package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewMenuLike;

@AllArgsConstructor
@Builder
@Schema(title = "리뷰 상세")
@Getter
public class MealReviewResponse {
    @Schema(description = "리뷰 식별자", example = "123")
    Long reviewId;

    @Schema(description = "작성자 식별자", example = "123")
    Long writerId;

    @Schema(description = "본인 글인지 여부(true/false)", example = "true")
    Boolean isWriter;

    @Schema(description = "작성자 닉네임", example = "숭시리시리")
    private String writerNickname;

    @Schema(description = "평점", example = "4")
    private Integer rating;

    @Schema(description = "리뷰 작성 날짜(format = yyyy-MM-dd)", example = "2023-04-07")
    private LocalDate writtenAt;

    @Schema(description = "리뷰 내용", example = "맛있습니당")
    private String content;

    @Schema(description = "리뷰 이미지 url 리스트", example = "[\"imgurl1\", \"imgurl2\"]")
    private List<String> imageUrls;

    @Schema(description = "좋아요한 메뉴명 리스트", example = "[\"메뉴1\", \"메뉴2\"]")
    private List<String> likedMenuNames;

    public static MealReviewResponse from(Review review, Long userId) {
        List<String> imageUrls = new ArrayList<>();
        review.getReviewImages().forEach(i -> imageUrls.add(i.getImageUrl()));

        List<String> likedMenuNames = review.getMenuLikes().stream()
                .filter(ReviewMenuLike::getIsLike)
                .map(like -> like.getMenu().getName())
                .collect(Collectors.toList());

        MealReviewResponseBuilder builder = MealReviewResponse.builder()
                .reviewId(review.getId())
                .rating(review.getRating())
                .writtenAt(review.getCreatedDate().toLocalDate())
                .content(review.getContent())
                .imageUrls(imageUrls)
                .likedMenuNames(likedMenuNames);

        if (review.getUser() == null) {
            return builder.writerId(null)
                    .writerNickname("알 수 없음")
                    .isWriter(false)
                    .build();
        }

        if (review.getUser().getId().equals(userId)) {
            return builder.writerId(review.getUser().getId())
                    .writerNickname(review.getUser().getNickname())
                    .isWriter(true)
                    .build();
        }

        return builder.writerId(review.getUser().getId())
                .writerNickname(review.getUser().getNickname())
                .isWriter(false)
                .build();
    }
}
