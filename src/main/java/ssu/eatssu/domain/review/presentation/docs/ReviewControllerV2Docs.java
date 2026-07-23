package ssu.eatssu.domain.review.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.dto.CreateMealReviewRequest;
import ssu.eatssu.domain.review.dto.CreateMenuReviewRequestV2;
import ssu.eatssu.domain.review.dto.MealReviewResponse;
import ssu.eatssu.domain.review.dto.MealReviewsV2Response;
import ssu.eatssu.domain.review.dto.MenuReviewsV2Response;
import ssu.eatssu.domain.review.dto.RestaurantReviewResponse;
import ssu.eatssu.domain.review.dto.ReviewDetail;
import ssu.eatssu.domain.review.dto.ReviewTranslationResponse;
import ssu.eatssu.domain.review.dto.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.dto.ValidMenuForViewResponse;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.dto.MyMealReviewResponse;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.global.handler.response.BaseResponse;

@Tag(name = "Review V2", description = "리뷰 V2 API")
public interface ReviewControllerV2Docs {

    @Operation(summary = "meal(식단)에 대한 리뷰 작성", description = "리뷰를 작성하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 작성 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<?> createMealReview(CreateMealReviewRequest createMealReviewRequest,
                                     CustomUserDetails customUserDetails);

    @Operation(summary = "특정 식당 모든 리뷰 정보 조회(리뷰 개수, 평점 등등)", description = """
            특정 식당 모든 리뷰 정보를 조회하는 API 입니다.<br><br>
            리뷰 개수, 별점 별 개수, 리뷰 평점, 좋아요 개수, 싫어요 개수를 조회합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "path parameter 누락", content = @Content(schema =
            @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<RestaurantReviewResponse> getRestaurantReviews(
            @Parameter(description = "restaurant") Restaurant restaurant);

    @Operation(summary = "meal(식단)에 대한 리뷰 리스트 조회", description = """
            리뷰 리스트를 조회하는 API 입니다.<br><br>
            커서 기반 페이지네이션으로 리뷰 리스트를 조회합니다.<br><br>
            페이징 기본 값 = {size=20, sort=date, direction=desc}<br><br>
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 리스트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<SliceResponse<MealReviewResponse>> getMealReviewList(
            @Parameter(description = "mealId") Long mealId,
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY) Long lastReviewId,
            CustomUserDetails customUserDetails);

    @Operation(summary = "리뷰 수정(글 수정)", description = """
            리뷰 내용을 수정하는 API 입니다.<br><br>
            글 수정만 가능하며 사진 수정은 지원하지 않습니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @ApiResponse(responseCode = "403", description = "리뷰에 대한 권한이 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema =
            @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<?> updateReview(@Parameter(description = "reviewId") Long reviewId,
                                 UpdateMealReviewRequest request,
                                 CustomUserDetails customUserDetails);

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "리뷰에 대한 권한이 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<?> deleteReview(@Parameter(description = "reviewId") Long reviewId,
                                 CustomUserDetails customUserDetails);

    @Operation(summary = "리뷰 번역", description = """
            리뷰 내용을 DeepL로 번역하는 API 입니다.<br><br>
            같은 리뷰/언어 조합은 캐시된 결과를 반환합니다.<br><br>
            현재는 language=EN만 지원합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 번역 성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 언어이거나 번역할 내용이 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "429", description = "번역 API 사용량 한도 초과", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "503", description = "번역 시간 초과 또는 실패", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<ReviewTranslationResponse> translateReview(
            @Parameter(description = "reviewId") Long reviewId,
            @Parameter(description = "번역 대상 언어(현재 EN만 지원)") Language language);

    @Operation(summary = "식단(변동 메뉴) 리뷰 정보 조회 V2(메뉴명, 평점 등등) [인증 토큰 필요 X]", description = """
            식단 리뷰 정보를 조회하는 API 입니다.<br><br>
            메뉴명 리스트, 리뷰 수, 메인 평점, 좋아요 개수, 싫어요 개수, 각 평점의 개수를 조회합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema =
            @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<MealReviewsV2Response> getMealReviews(@Parameter(description = "mealId") Long mealId);

    @Operation(summary = "고정 메뉴 리뷰 정보 조회 V2(메뉴명, 평점 등등) [인증 토큰 필요 X]", description = """
            고정 메뉴 리뷰 정보를 조회하는 API 입니다.<br><br>
            메뉴명, 리뷰 수, 메인 평점, 좋아요 개수, 싫어요 개수, 각 평점의 개수를 조회합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema =
            @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<MenuReviewsV2Response> getMainReviews(@Parameter(description = "menuId") Long menuId);

    @Operation(summary = "menu 에 대한 리뷰 리스트 조회", description = """
            리뷰 리스트를 조회하는 API 입니다.<br><br>
            커서 기반 페이지네이션으로 리뷰 리스트를 조회합니다.<br><br>
            페이징 기본 값 = {size=20, sort=date, direction=desc}<br><br>
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 리스트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<SliceResponse<ReviewDetail>> getMenuReviewList(
            @Parameter(description = "menuId(고정메뉴)") Long menuId,
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY) Long lastReviewId,
            @ParameterObject Pageable pageable,
            CustomUserDetails customUserDetails);

    @Operation(summary = "menu에 대한 리뷰 작성", description = "리뷰를 작성하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 작성 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<?> createMenuReview(CreateMenuReviewRequestV2 createMenuReviewRequestV2,
                                     CustomUserDetails customUserDetails);

    @Operation(summary = "내가 쓴 리뷰 리스트 조회", description = "내가 쓴 리뷰 리스트를 조회하는 API V2 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 쓴 리뷰 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<SliceResponse<MyMealReviewResponse>> getMyReviews(
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY) Long lastReviewId,
            @ParameterObject Pageable pageable,
            CustomUserDetails customUserDetails);

    @Operation(summary = "식단 id를 통해 리뷰 작성할 수 있는 메뉴들 조회", description = "리뷰 작성할 수 있는 메뉴들 조회하는 API입니다. (노션 문서 > 리뷰v2 기능명세서> 리뷰에 제외되는 메뉴 참고")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 작성할 수 있는 메뉴들 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<ValidMenuForViewResponse> getValidMenuForReview(@Parameter(description = "mealId") Long mealId);
}
