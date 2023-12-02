package ssu.eatssu.web.restaurant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.Meal;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.enums.MenuTypeGroup;
import ssu.eatssu.domain.enums.RestaurantName;
import ssu.eatssu.domain.enums.TimePart;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.response.BaseResponse;
import ssu.eatssu.service.MenuService;
import ssu.eatssu.web.restaurant.dto.MenuReqDto.AddTodayMenuList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static ssu.eatssu.response.BaseResponseStatus.*;
import static ssu.eatssu.web.restaurant.dto.MenuResDto.*;

@Slf4j
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "메뉴 API")
public class MenuController {

    private final MenuService menuService;

    /**
     * 특정 식당의 식단 조회
     */
    @Operation(summary = "변동메뉴 식단 리스트 조회 By 식당", description = "변동메뉴 식단 리스트 조회(학생식당, 도담, 기숙사 식당)")
    @GetMapping("/today-meal")
    public ResponseEntity<List<TodayMeal>> todayMealList(@Parameter(description = "날짜(yyyyMMdd)") @RequestParam(
            "date")
                                                                 String date,
                                                         @Parameter(description = "식당이름") @RequestParam("restaurant")
                                                                 RestaurantName restaurantName,
                                                         @Parameter(description = "시간대") @RequestParam("time")
                                                                 TimePart timePart) throws ParseException {
        if (MenuTypeGroup.isChange(restaurantName)) {
            List<Meal> mealList = menuService.findTodayMeals(timePart, date, restaurantName);
            List<TodayMeal> todayMealList = new ArrayList<>();
            for (Meal meal : mealList) {
                TodayMeal todayMeal = TodayMeal.from(meal);
                todayMealList.add(todayMeal);
            }
            return ResponseEntity.ok(todayMealList);
        } else {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }
    }


    /**
     * 고정메뉴 조회
     */
    @Operation(summary = "고정 메뉴 리스트 조회", description = "고정 메뉴 리스트 조회(푸드코트, 스낵코너, 더 키친)")
    @GetMapping("/fix-menu")
    public ResponseEntity<FixMenuList> fixMenuList(@Parameter(description = "식당이름") @RequestParam("restaurant")
                                                           RestaurantName restaurantName) {
        if (MenuTypeGroup.isFix(restaurantName)) {
            List<Menu> menuList = menuService.findFixMenuByRestaurant(restaurantName);
            FixMenuList fixMenuList = FixMenuList.from(menuList);
            return ResponseEntity.ok(fixMenuList);
        } else {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }
    }


    /**
     * 특정 식당의 특정 날짜 식단 추가하기
     */
    @Operation(summary = "특정 식당 식단 추가", description = "특정 식당의 식단 추가")
    @PostMapping("/")
    public ResponseEntity todayMenuAdd(@Parameter(description = "날짜(yyyyMMdd)") @RequestParam("date")
                                                 String date,
                                         @Parameter(description = "식당이름") @RequestParam("restaurant")
                                                 RestaurantName restaurantName,
                                         @Parameter(description = "시간대") @RequestParam("time")
                                                 TimePart timePart,
                                         @RequestBody AddTodayMenuList addTodayMenuList) throws ParseException {

        if (MenuTypeGroup.isChange(restaurantName)) {
            try{
               if(menuService.dupliicateMealCheck(timePart,date, restaurantName, addTodayMenuList)){//이미 추가된 식단이면
                   log.info("식단 중복 발견!");
                   return ResponseEntity.ok(HttpStatus.OK);
               }
            }catch (ParseException e){
                throw new BaseException(INVALID_DATE);
            }
            menuService.addMeal(timePart, date, restaurantName, addTodayMenuList);
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }
    }

    /**
     * mealId로 menuId, name 리스트 조회
     */
    @Operation(summary = "메뉴 정보 조회 By mealId", description = "메뉴 정보(Id, name) 조회 By mealId")
    @GetMapping("/menus")
    public ResponseEntity menuList(@Parameter(description = "mealId") @RequestParam("mealId") Long mealId) {
        MenuList menuList = menuService.findAllMenu(mealId);
        return ResponseEntity.ok(menuList);
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return new BaseResponse<>(e.getStatus());
    }

}
