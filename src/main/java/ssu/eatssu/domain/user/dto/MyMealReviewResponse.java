package ssu.eatssu.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.review.dto.MenuIdNameLikeDto;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewMenuLike;
import ssu.eatssu.domain.review.utils.MenuFilterUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Schema(title = "리뷰 상세 - 내 리뷰 리스트 조회 용")
@Getter
@Slf4j
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

    public static MyMealReviewResponse from(Review review) {
        List<String> imgUrlList = new ArrayList<>();
        review.getReviewImages().forEach(i -> imgUrlList.add(i.getImageUrl()));

        Set<Long> likedMenuIds = review.getMenuLikes().stream()
                                       .filter(ReviewMenuLike::getIsLike)
                                       .map(like -> like.getMenu().getId())
                                       .collect(Collectors.toSet());


        List<MenuIdNameLikeDto> menuNames;

        if (review.getMeal() != null) {
            menuNames = review.getMeal().getMenus().stream()
                              .filter(menu -> !MenuFilterUtil.isExcludedFromReview(menu.getName()))
                              .map(menu -> new MenuIdNameLikeDto(
                                      menu.getId(),
                                      menu.getName(),
                                      likedMenuIds.contains(menu.getId())
                              ))
                              .toList();
        } else {
            menuNames = Collections.singletonList(
                    new MenuIdNameLikeDto(
                            review.getMenu().getId(),
                            review.getMenu().getName(),
                            likedMenuIds.contains(review.getMenu().getId())
                    )
                                                 );
        }

        log.info("ratings = {}", review.getRatings());
        if (review.getRatings() != null) {
            log.info("mainRating = {}", review.getRatings().getMainRating());
        }
        log.info("legacy rating = {}", review.getRating());

        Ratings ratings = review.getRatings();
        Integer rating = null;

        if (ratings != null && ratings.getMainRating() != null) {
            rating = ratings.getMainRating();
        } else if (review.getRating() != null) {
            rating = review.getRating();
        }

        return MyMealReviewResponse
                .builder()
                .reviewId(review.getId())
                .rating(rating)
                .writtenAt(review.getCreatedDate().toLocalDate())
                .content(review.getContent())
                .imageUrls(imgUrlList)
                .menuList(menuNames)
                .build();
    }
}
