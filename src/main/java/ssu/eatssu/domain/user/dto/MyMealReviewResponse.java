package ssu.eatssu.domain.user.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.review.entity.Review;

@AllArgsConstructor
@Builder
@Schema(title = "리뷰 상세 - 내 리뷰 리스트 조회 용")
@Getter
public class MyMealReviewResponse {
	@Schema(description = "리뷰 식별자", example = "123")
	Long reviewId;

	@Schema(description = "평점", example = "4")
	private Integer rating;

	@Schema(description = "리뷰 작성 날짜(format = yyyy-MM-dd)", example = "2023-04-07")
	private LocalDate writtenAt;

	@Schema(description = "리뷰 내용", example = "맛있습니당")
	private String content;

	@Schema(description = "리뷰 이미지 url 리스트", example = "[\"imgurl1\", \"imgurl2\"]")
	private List<String> imageUrls;

	public static MyMealReviewResponse from(Review review) {
		List<String> imgUrlList = new ArrayList<>();
		review.getReviewImages().forEach(i -> imgUrlList.add(i.getImageUrl()));

		return MyMealReviewResponse.builder()
								   .reviewId(review.getId())
								   .rating(review.getRating())
								   .writtenAt(review.getCreatedDate().toLocalDate())
								   .content(review.getContent())
								   .imageUrls(imgUrlList)
								   .build();
	}
}
