package ssu.eatssu.web.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.review.Review;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@Schema(title = "리뷰 상세")
@Getter
public class ReviewDetail {

    @Schema(description = "리뷰 식별자", example = "123")
    Long reviewId;

    @Schema(description = "메뉴 이름", example = "콥샐러드")
    String menu;

    @Schema(description = "작성자 식별자", example = "123")
    Long writerId;

    @Schema(description = "본인 글인지 여부(true/false)", example = "true")
    Boolean isWriter;

    @Schema(description = "작성자 닉네임", example = "숭시리시리")
    private String writerNickname;

    @Schema(description = "평점-메인", example = "4")
    private Integer mainRate;

    @Schema(description = "평점-양", example = "4")
    private Integer amountRate;

    @Schema(description = "평점-맛", example = "4")
    private Integer tasteRate;

    @Schema(description = "리뷰 작성 날짜(format = yyyy-MM-dd)", example = "2023-04-07")
    private LocalDate writeDate;

    @Schema(description = "리뷰 내용", example = "맛있습니당")
    private String content;

    @Schema(description = "리뷰 이미지 url 리스트", example = "[\"imgurl1\", \"imgurl2\"]")
    private List<String> imgUrlList;

    public static ReviewDetail from(Review review, Long userId) {

        List<String> imgUrlList = new ArrayList<>();
        review.getReviewImages().forEach(i -> imgUrlList.add(i.getImageUrl()));
        if (review.getUser() == null) {//탈퇴한 유저의 리뷰인 경우
            return ReviewDetail.builder()
	.reviewId(review.getId())
	.writerId(null).writerNickname("알 수 없음")
	.mainRate(review.getRate().getMainRate())
	.amountRate(review.getRate().getAmountRate())
	.tasteRate(review.getRate().getTasteRate())
	.writeDate(review.getCreatedDate().toLocalDate()).content(review.getContent())
	.isWriter(false).imgUrlList(imgUrlList).menu(review.getMenu().getName())
	.build();
        } else {
            boolean isWriter = review.getUser().getId().equals(userId);
            return ReviewDetail.builder()
	.reviewId(review.getId())
	.writerId(review.getUser().getId()).writerNickname(review.getUser().getNickname())
	.mainRate(review.getRate().getMainRate())
	.amountRate(review.getRate().getAmountRate())
	.tasteRate(review.getRate().getTasteRate())
	.writeDate(review.getCreatedDate().toLocalDate()).content(review.getContent())
	.isWriter(isWriter).imgUrlList(imgUrlList).menu(review.getMenu().getName())
	.build();
        }

    }
}
