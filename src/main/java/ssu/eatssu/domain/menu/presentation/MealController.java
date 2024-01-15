package ssu.eatssu.domain.menu.presentation;

import static ssu.eatssu.handler.response.BaseResponseStatus.NOT_SUPPORT_RESTAURANT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.enums.TimePart;
import ssu.eatssu.domain.menu.dto.MenuRequest.AddTodayMenuList;
import ssu.eatssu.domain.menu.dto.MenuResponse.TodayMeal;
import ssu.eatssu.domain.menu.entity.MenuType;
import ssu.eatssu.domain.menu.service.MenuService;
import ssu.eatssu.domain.restaurant.RestaurantName;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meals")
@Tag(name = "Menu", description = "메뉴 API")
public class MealController {

    private final MenuService menuService;

    /**
     * 식단 추가
     * <p>변동메뉴 식당(학생식당, 도담, 기숙사 식당)의 특정날짜(yyyyMMdd), 특정시간대(아침/점심/저녁)에 해당하는 식단을 추가.
     * 이미 존재하는 식단일 경우 중복저장 되지 않도록 처리한다.(별도의 ErrorResponse 응답 X)</p>
     */
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
    @PostMapping("/create")
    public BaseResponse<?> createMeal(@Parameter(description = "날짜(yyyyMMdd)")
    @RequestParam("date") String date,
        @Parameter(description = "식당이름")
        @RequestParam("restaurant") RestaurantName restaurantName,
        @Parameter(description = "시간대")
        @RequestParam("time") TimePart timePart,
        @RequestBody AddTodayMenuList addTodayMenuList) {
        if (MenuType.isFixed(restaurantName)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }

        menuService.createMeal(timePart, date, restaurantName, addTodayMenuList);
        return BaseResponse.success();
    }

    @Operation(summary = "변동 메뉴 식단 리스트 조회", description = """
        변동 메뉴 식단 리스트를 조회하는 API 입니다.
        변동 메뉴 식당 (학생 식당, 도담, 기숙사 식당) 의 특정날짜(yyyyMMdd), 특정시간대(아침/점심/저녁)에 해당하는 식단 목록을 조회합니다.
        일반적으로 학생식당과 도담의 경우 식단 여러개가 조회되고 기숙사식당은 한개만 조회됩니다.)
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "식단 리스트 조회 성공", content = @Content(schema = @Schema(implementation = ssu.eatssu.handler.response.BaseResponse.class))),
        @ApiResponse(responseCode = "400", description = "지원 하지 않는 식당 (고정 메뉴 식당)", content = @Content(schema = @Schema(implementation = ssu.eatssu.handler.response.BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 식당", content = @Content(schema = @Schema(implementation = ssu.eatssu.handler.response.BaseResponse.class)))
    })
    @GetMapping("/list")
    public BaseResponse<List<TodayMeal>> getMeal(
        @Parameter(description = "날짜(yyyyMMdd)")
        @RequestParam("date") String date,
        @Parameter(description = "식당이름")
        @RequestParam("restaurant") RestaurantName restaurantName,
        @Parameter(description = "시간대")
        @RequestParam("time") TimePart timePart) {
        if (MenuType.isFixed(restaurantName)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }

        return ssu.eatssu.handler.response.BaseResponse.success(
            menuService.findMealList(timePart, date, restaurantName));
    }


    /**
     * 식단 삭제
     * <p>식단식별자(mealId)에 해당하는 식단을 삭제한다.</p>
     */
    @Operation(summary = "식단 삭제", description = "식단을 삭제하는 API 입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "식단 삭제 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/meal/{mealId}")
    public BaseResponse<?> deleteMeal(
        @Parameter(description = "mealId") @PathVariable("mealId") Long mealId) {
        //todo : 관리자 API
        menuService.deleteMeal(mealId);
        return BaseResponse.success();
    }
}
