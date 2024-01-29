package ssu.eatssu.domain.menu.presentation;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.menu.dto.MenuRequest;
import ssu.eatssu.domain.menu.dto.MenuResponse.MealInformationResponse;
import ssu.eatssu.domain.menu.entity.TimePart;
import ssu.eatssu.domain.menu.service.MenuService;
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
@Tag(name = "Menu", description = "메뉴 API")
public class MealController {

    private final MenuService menuService;

    @Operation(summary = "식단 추가", description = """
            식단을 추가하는 API 입니다.<br><br>
            변동메뉴 식당(학생식당, 도담, 기숙사 식당)의 특정날짜(yyyyMMdd), 특정시간대(아침/점심/저녁)에 해당하는 식단을 추가합니다.<br><br>
            이미 존재하는 식단일 경우 중복저장 되지 않도록 처리합니다.(별도의 ErrorResponse 응답 X)
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식단 추가 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 식당(고정 메뉴 식당)", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 날짜형식", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식당", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("")
    public BaseResponse<?> createMeal(
            @Parameter(schema = @Schema(type = "string", format = "date", example = "20240101")) @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") Date date,
            @Parameter(description = "식당이름") @RequestParam("restaurant") Restaurant restaurant,
            @Parameter(description = "시간대") @RequestParam("time") TimePart timePart,
            @RequestBody MenuRequest.MealCreateRequest mealCreateRequest) {
        if (RestaurantType.isFixedType(restaurant)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }

        menuService.createMeal(date, restaurant, timePart, mealCreateRequest);
        return BaseResponse.success();
    }

    @Operation(summary = "변동 메뉴 식단 리스트 조회", description = """
            변동 메뉴 식단 리스트를 조회하는 API 입니다.
            변동 메뉴 식당 (학생 식당, 도담, 기숙사 식당) 의 특정날짜(yyyyMMdd), 특정시간대(아침/점심/저녁)에 해당하는 식단 목록을 조회합니다.
            일반적으로 학생식당과 도담의 경우 식단 여러개가 조회되고 기숙사식당은 한개만 조회됩니다.)
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식단 리스트 조회 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "지원 하지 않는 식당 (고정 메뉴 식당)", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 식당", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("")
    public BaseResponse<List<MealInformationResponse>> getMeal(
            @Parameter(schema = @Schema(type="string" ,format = "date", example = "20240101")) @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") Date date,
            @Parameter(description = "식당 이름") @RequestParam("restaurant") Restaurant restaurant,
            @Parameter(description = "시간대") @RequestParam("time") TimePart timePart) {
        if (RestaurantType.isFixedType(restaurant)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }

        return BaseResponse.success(
                menuService.findSpecificMeals(date, restaurant, timePart));
    }

    @Operation(summary = "식단 삭제", description = "식단을 삭제하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식단 삭제 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/{mealId}")
    public BaseResponse<?> deleteMeal(
            @Parameter(description = "mealId") @PathVariable("mealId") Long mealId) {
        menuService.deleteMeal(mealId);
        return BaseResponse.success();
    }
}
