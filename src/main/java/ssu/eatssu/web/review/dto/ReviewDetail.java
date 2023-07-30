package ssu.eatssu.web.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.Review;

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
    private Integer mainGrade;

    @Schema(description = "평점-양", example = "4")
    private Integer amountGrade;

    @Schema(description = "평점-맛", example = "4")
    private Integer tasteGrade;

    @Schema(description = "리뷰 작성 날짜(format = yyyy-MM-dd)", example = "2023-04-07")
    private LocalDate writeDate;

    @Schema(description = "리뷰 내용", example = "맛있습니당")
    private String content;

    @Schema(description = "리뷰 이미지 url 리스트", example = "[\"imgurl1\", \"imgurl2\"]")
    private List<String> imgUrlList;

    public static ReviewDetail from(Review review, Long userId) {

        List<String> imgUrlList = new ArrayList<>();
        review.getReviewImgs().forEach(i -> imgUrlList.add(i.getImageUrl()));
        boolean isWriter = review.getUser().getId().equals(userId);

        return ReviewDetail.builder()
                .reviewId(review.getId())
                .writerId(review.getUser().getId()).writerNickname(review.getUser().getNickname())
                .mainGrade(review.getMainGrade()).amountGrade(review.getAmountGrade()).tasteGrade(review.getTasteGrade())
                .writeDate(review.getCreatedDate().toLocalDate()).content(review.getContent())
                .isWriter(isWriter).imgUrlList(imgUrlList).menu(review.getMenu().getName())
                .build();
    }
}
