package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewMenuLike;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Schema(description = "메뉴 리스트", example = """
    [
      {
        "menuId": 3143,
        "name": "생고기제육볶음",
        "isLike": true
      },
      {
        "menuId": 3144,
        "name": "오징어초무침",
        "isLike": false
      }
    ]
    """)
    private List<MenuIdNameLikeDto> menuList;

    public static MealReviewResponse from(Review review,
                                          Long userId,
                                          List<ValidMenuForViewResponse.MenuDto> validMenus) {
        List<String> imageUrls = new ArrayList<>();
        review.getReviewImages().forEach(i -> imageUrls.add(i.getImageUrl()));

        // 좋아요한 메뉴 ID 모음
        Set<Long> likedMenuIds = review.getMenuLikes().stream()
                                       .filter(ReviewMenuLike::getIsLike)
                                       .map(like -> like.getMenu().getId())
                                       .collect(Collectors.toSet());

        List<MenuIdNameLikeDto> menuNames = validMenus.stream()
                                                      .map(valid -> new MenuIdNameLikeDto(
                                                              valid.getMenuId(),
                                                              valid.getName(),
                                                              likedMenuIds.contains(valid.getMenuId())
                                                      ))
                                                      .toList();

        MealReviewResponseBuilder builder = MealReviewResponse.builder()
                                                              .reviewId(review.getId())
                                                              .rating(review.getRating())
                                                              .writtenAt(review.getCreatedDate().toLocalDate())
                                                              .content(review.getContent())
                                                              .imageUrls(imageUrls)
                                                              .menuList(menuNames);

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
