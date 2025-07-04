package ssu.eatssu.domain.menu.presentation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.presentation.dto.request.CreateMealRequest;
import ssu.eatssu.domain.menu.presentation.dto.request.MealCreateWithPriceRequest;
import ssu.eatssu.domain.menu.presentation.dto.response.MealDetailResponse;
import ssu.eatssu.domain.menu.presentation.dto.response.MenusInMealResponse;
import ssu.eatssu.domain.menu.service.MealService;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.Date;
import java.util.List;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_SUPPORT_RESTAURANT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meals")
@Tag(name = "Meal", description = "식단 API")
public class MealController {

    private final MealService mealService;

    @Operation(summary = "식단 추가 [인증 토큰 필요 X]", description = """
            식단을 추가하는 API 입니다.<br><br>
            변동메뉴 식당(학생식당, 도담, 기숙사 식당)의 특정날짜(yyyyMMdd), 특정시간대(아침/점심/저녁)에 해당하는 식단을 추가합니다.<br><br>
            이미 존재하는 식단일 경우 중복저장 되지 않도록 처리합니다.(별도의 ErrorResponse 응답 X)
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식단 추가 성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 식당(고정 메뉴 식당)", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 날짜형식", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식당", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("")
    public BaseResponse<Void> createMeal(
            @Parameter(schema = @Schema(type = "string", format = "date", example = "20240101")) @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") Date date,
            @Parameter(description = "식당이름") @RequestParam("restaurant") Restaurant restaurant,
            @Parameter(description = "시간대") @RequestParam("time") TimePart timePart,
            @RequestBody CreateMealRequest mealCreateRequest) {
        if (RestaurantType.isFixedType(restaurant)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }

        mealService.createMeal(date, restaurant, timePart, mealCreateRequest);
        return BaseResponse.success();
    }

    @Operation(summary = "식단 추가 [인증 토큰 필요 X]", description = """
            식단을 추가하는 API 입니다.<br><br>
            변동메뉴 식당(학생식당, 도담, 기숙사 식당)의 특정날짜(yyyyMMdd), 특정시간대(아침/점심/저녁)에 해당하는 식단을 추가합니다.<br><br>
            이미 존재하는 식단일 경우 중복저장 되지 않도록 처리합니다.(별도의 ErrorResponse 응답 X)
            가격을 외부에서 입력받습니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식단 추가 성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 식당(고정 메뉴 식당)", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 날짜형식", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식당", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/with-price")
    public BaseResponse<Void> createMealWithPrice(
            @Parameter(schema = @Schema(type = "string", format = "date", example = "20240101")) @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") Date date,
            @Parameter(description = "식당이름") @RequestParam("restaurant") Restaurant restaurant,
            @Parameter(description = "시간대") @RequestParam("time") TimePart timePart,
            @RequestBody MealCreateWithPriceRequest request) {
        if (RestaurantType.isFixedType(restaurant)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }

        mealService.createMealWithPrice(date, restaurant, timePart, request);
        return BaseResponse.success();
    }

    @Operation(summary = "변동 메뉴 식단 리스트 조회 [인증 토큰 필요 X]", description = """
            변동 메뉴 식단 리스트를 조회하는 API 입니다.
            변동 메뉴 식당 (학생 식당, 도담, 기숙사 식당) 의 특정날짜(yyyyMMdd), 특정시간대(아침/점심/저녁)에 해당하는 식단 목록을 조회합니다.
            일반적으로 학생식당과 도담의 경우 식단 여러 개가 조회되고 기숙사식당은 한 개만 조회됩니다.)
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식단 리스트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "지원 하지 않는 식당 (고정 메뉴 식당)", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 식당", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("")
    public BaseResponse<List<MealDetailResponse>> getMealDetail(
            @Parameter(schema = @Schema(type = "string", format = "date", example = "20240101")) @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") Date date,
            @Parameter(description = "식당 이름") @RequestParam("restaurant") Restaurant restaurant,
            @Parameter(description = "시간대") @RequestParam("time") TimePart timePart) {

        return BaseResponse.success(
                mealService.getMealDetailsByDateAndRestaurantAndTimePart(date, restaurant, timePart));
    }

    @Operation(summary = "식단 삭제 [인증 토큰 필요 X]", description = "식단을 삭제하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식단 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/{mealId}")
    public BaseResponse<?> deleteMeal(
            @Parameter(description = "mealId") @PathVariable("mealId") Long mealId) {

        mealService.deleteByMealId(mealId);
        return BaseResponse.success();
    }

    @Operation(summary = "메뉴 정보 리스트 조회 [인증 토큰 필요 X]", description = """
            메뉴 정보 리스트를 조회하는 API 입니다.<br><br>
            식단식별자(mealId)로 해당 식단에 속하는 메뉴 정보 목록을 조회합니다.")
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 정보 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/{mealId}/menus-info")
    public BaseResponse<MenusInMealResponse> getMenusInMeal(@Parameter(description = "mealId")
                                                            @PathVariable("mealId") Long mealId) {
        return BaseResponse.success(mealService.getMenusInMealByMealId(mealId));
    }
}
