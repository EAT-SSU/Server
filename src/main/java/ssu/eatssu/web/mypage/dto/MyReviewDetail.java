package ssu.eatssu.web.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.Review;
import ssu.eatssu.domain.enums.ReviewTag;
import ssu.eatssu.web.review.dto.ReviewDetail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@Schema(title = "리뷰 상세 - 내 리뷰 리스트 조회 용")
@Getter
public class MyReviewDetail {

    @Schema(description = "별점", example = "4")
    private Integer grade;

    @Schema(description = "리뷰 작성 날짜(format = yyyyMMdd)", example = "20230407")
    private LocalDate writeDate;

    @Schema(description = "메뉴 이름", example = "돈까스")
    private String menuName;

    @Schema(description = "리뷰 내용", example = "맛있습니당")
    private String content;

    @Schema(description = "리뷰 태그 리스트", example = "[\"가성비가 좋아요\", \"든든한 한끼\"]")
    private List<String> tagList;

    @Schema(description = "리뷰 이미지 url 리스트", example = "[\"imgurl1\", \"imgurl2\"]")
    private List<String> imgUrlList;

    public static MyReviewDetail from(Review review){
        List<String> tagList = new ArrayList<>();
        review.tagsToList().stream().forEach(tag -> tagList.add(tag.getKrName()));

        List<String> imgUrlList = new ArrayList<>();
        review.getReviewImgs().stream().forEach(i->imgUrlList.add(i.getImageUrl()));

        return MyReviewDetail.builder()
                .grade(review.getGrade()).writeDate(review.getCreatedDate().toLocalDate()).content(review.getContent())
                .tagList(tagList).imgUrlList(imgUrlList)
                .menuName(review.getMenu().getName())
                .build();
    }
}
