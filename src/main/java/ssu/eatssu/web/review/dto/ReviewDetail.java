package ssu.eatssu.web.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.Review;
import ssu.eatssu.domain.enums.ReviewTag;

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

    @Schema(description = "작성자 식별자", example = "123")
    Long writerId;

    @Schema(description = "작성자 닉네임", example = "숭시리시리")
    private String writerNickname;

    @Schema(description = "별점", example = "4")
    private Integer grade;

    @Schema(description = "리뷰 작성 날짜(format = yyyy-MM-dd)", example = "2023-04-07")
    private LocalDate writeDate;

    @Schema(description = "리뷰 내용", example = "맛있습니당")
    private String content;

    @Schema(description = "리뷰 태그 리스트", example = "[\"가성비가 좋아요\", \"든든한 한끼\"]")
    private List<String> tagList;

    @Schema(description = "리뷰 이미지 url 리스트", example = "[\"imgurl1\", \"imgurl2\"]")
    private List<String> imgUrlList;

    public static ReviewDetail from(Review review){
        List<String> tagList = new ArrayList<>();
        review.tagsToList().stream().forEach(tag -> tagList.add(tag.getKrName()));

        List<String> imgUrlList = new ArrayList<>();
        review.getReviewImgs().stream().forEach(i->imgUrlList.add(i.getImageUrl()));

        return ReviewDetail.builder()
                .reviewId(review.getId())
                .writerId(review.getUser().getId()).writerNickname(review.getUser().getNickname())
                .grade(review.getGrade()).writeDate(review.getCreatedDate().toLocalDate()).content(review.getContent())
                .tagList(tagList).imgUrlList(imgUrlList)
                .build();
    }
}
