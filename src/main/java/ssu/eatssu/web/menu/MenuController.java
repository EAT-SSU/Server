package ssu.eatssu.web.menu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.Meal;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.enums.MenuTypeGroup;
import ssu.eatssu.domain.enums.RestaurantName;
import ssu.eatssu.domain.enums.TimePart;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.response.BaseResponse;
import ssu.eatssu.service.MenuService;
import ssu.eatssu.web.menu.dto.MenuReqDto.AddTodayMenuList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static ssu.eatssu.response.BaseResponseStatus.INVALID_DATE;
import static ssu.eatssu.response.BaseResponseStatus.NOT_SUPPORT_RESTAURANT;
import static ssu.eatssu.web.menu.dto.MenuResDto.*;

@Slf4j
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "메뉴 API")
public class MenuController {

    private final MenuService menuService;

    /**
     * 식단 목록 조회
     * <p>변동메뉴 식당(학생식당, 도담, 기숙사 식당)의 특정날짜(yyyyMMdd), 특정시간대(아침/점심/저녁)에 해당하는 식단 목록을 조회한다.
     * 일반적으로 학생식당과 도담의 경우 식단 여러개가 조회되고 기숙사식당은 한개만 조회된다.</p>
     */
    @Operation(summary = "변동메뉴 식단 리스트 조회 By 식당", description = "변동메뉴 식단 리스트 조회(학생식당, 도담, 기숙사 식당)")
    @GetMapping("/today-meal") //todo today-meal은 아닌것 같음
    public BaseResponse<List<TodayMeal>> getMealList(@Parameter(description = "날짜(yyyyMMdd)")
                                                     @RequestParam("date") String date,
                                                     @Parameter(description = "식당이름")
                                                     @RequestParam("restaurant") RestaurantName restaurantName,
                                                     @Parameter(description = "시간대")
                                                     @RequestParam("time") TimePart timePart) throws ParseException { //todo Exceiption service단에서 처리
        if (MenuTypeGroup.isChange(restaurantName)) {
            List<Meal> mealList = menuService.findMealList(timePart, date, restaurantName);
            List<TodayMeal> todayMealList = new ArrayList<>();
            for (Meal meal : mealList) {
                TodayMeal todayMeal = TodayMeal.from(meal);
                todayMealList.add(todayMeal);
            }
            return new BaseResponse<>(todayMealList);
        } else {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }
    }


    /**
     * 고정 메뉴 목록 조회
     * <p>메뉴가 고정된 식당(푸드코트, 스낵코너, <s>더 키친</s>)의 메뉴 목록을 조회한다.</p>
     */
    @Operation(summary = "고정 메뉴 리스트 조회", description = "고정 메뉴 리스트 조회(푸드코트, 스낵코너, 더 키친)")
    @GetMapping("/fix-menu")
    public BaseResponse<FixMenuList> getFixMenuList(@Parameter(description = "식당이름")
                                                    @RequestParam("restaurant") RestaurantName restaurantName) {
        if (MenuTypeGroup.isFix(restaurantName)) {
            List<Menu> menuList = menuService.findFixMenuList(restaurantName);
            FixMenuList fixMenuList = FixMenuList.from(menuList);
            return new BaseResponse<>(fixMenuList);
        } else {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }
    }


    /**
     * 식단 추가
     * <p>변동메뉴 식당(학생식당, 도담, 기숙사 식당)의 특정날짜(yyyyMMdd), 특정시간대(아침/점심/저녁)에 해당하는 식단을 추가.
     * 이미 존재하는 식단일 경우 중복저장 되지 않도록 처리한다.(별도의 ErrorResponse 응답 X)</p>
     */
    @Operation(summary = "특정 식당 식단 추가", description = "특정 식당의 식단 추가")
    @PostMapping("/")
<<<<<<< Updated upstream
    public BaseResponse<String> addMeal(@Parameter(description = "날짜(yyyyMMdd)")
=======
    public BaseResponse<?> addMeal(@Parameter(description = "날짜(yyyyMMdd)")
>>>>>>> Stashed changes
                                        @RequestParam("date") String date,
                                        @Parameter(description = "식당이름")
                                        @RequestParam("restaurant") RestaurantName restaurantName,
                                        @Parameter(description = "시간대")
                                        @RequestParam("time") TimePart timePart,
                                        @RequestBody AddTodayMenuList addTodayMenuList) throws ParseException {
        //todo Exceiption service단에서 처리
        if (MenuTypeGroup.isChange(restaurantName)) {
            try {
                if (menuService.isExistMeal(timePart, date, restaurantName, addTodayMenuList)) {//이미 추가된 식단이면
                    log.info("식단 중복 발견!");
                    return new BaseResponse<>("Duplicated");
                }
            } catch (ParseException e) {
                throw new BaseException(INVALID_DATE);
            }
            menuService.createMeal(timePart, date, restaurantName, addTodayMenuList);
            return new BaseResponse<>("");
        } else {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }
    }

    /**
     * 식단의 메뉴정보 목록 조회
     * <p>식단식별자(mealId)로 식단에 속하는 메뉴정보(menuId, name) 목록을 조회한다.</p>
     */
    @Operation(summary = "메뉴 정보 조회 By mealId", description = "메뉴 정보(Id, name) 조회 By mealId")
    @GetMapping("/menus")
    public BaseResponse<MenuList> getMenuListInMeal(@Parameter(description = "mealId")
                                                    @RequestParam("mealId") Long mealId) {
        MenuList menuList = menuService.findMenuListInMeal(mealId);
        return new BaseResponse<>(menuList);
    }

    /**
     * 식단 삭제
     * <p>식단식별자(mealId)에 해당하는 식단을 삭제한다.</p>
     */
    @Operation(summary = "식단 삭제", description = "mealId로 meal 삭제")
    @DeleteMapping("/meal/{mealId}")
<<<<<<< Updated upstream
    public BaseResponse<String> deleteMeal(@Parameter(description = "mealId") @PathVariable("mealId") Long mealId) {
=======
    public BaseResponse<?> deleteMeal(@Parameter(description = "mealId") @PathVariable("mealId") Long mealId) {
>>>>>>> Stashed changes
        List<Long> menuIdList = menuService.deleteMeal(mealId);
        menuService.cleanupGarbageMenu(menuIdList);
        return new BaseResponse<>("");
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return new BaseResponse<>(e.getStatus());
    }

}
