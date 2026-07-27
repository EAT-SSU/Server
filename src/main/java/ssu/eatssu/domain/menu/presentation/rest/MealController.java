package ssu.eatssu.domain.menu.presentation.rest;

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
import ssu.eatssu.domain.menu.presentation.rest.docs.MealControllerDocs;
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
public class MealController implements MealControllerDocs {

    private final MealService mealService;

    @Override
    @PostMapping("")
    public BaseResponse<Void> createMeal(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") Date date,
            @RequestParam("restaurant") Restaurant restaurant,
            @RequestParam("time") TimePart timePart,
            @RequestBody CreateMealRequest mealCreateRequest) {
        if (RestaurantType.isFixedType(restaurant)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }

        mealService.createMeal(date, restaurant, timePart, mealCreateRequest);
        return BaseResponse.success();
    }

    @Override
    @PostMapping("/with-price")
    public BaseResponse<Void> createMealWithPrice(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") Date date,
            @RequestParam("restaurant") Restaurant restaurant,
            @RequestParam("time") TimePart timePart,
            @RequestBody MealCreateWithPriceRequest request) {
        if (RestaurantType.isFixedType(restaurant)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }

        mealService.createMealWithPrice(date, restaurant, timePart, request);
        return BaseResponse.success();
    }

    @Override
    @GetMapping("")
    public BaseResponse<List<MealDetailResponse>> getMealDetail(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") Date date,
            @RequestParam("restaurant") Restaurant restaurant,
            @RequestParam("time") TimePart timePart) {

        return BaseResponse.success(
                mealService.getMealDetailsByDateAndRestaurantAndTimePart(date, restaurant, timePart));
    }

    @Override
    @DeleteMapping("/{mealId}")
    public BaseResponse<?> deleteMeal(
            @PathVariable("mealId") Long mealId) {

        mealService.deleteByMealId(mealId);
        return BaseResponse.success();
    }

    @Override
    @GetMapping("/{mealId}/menus-info")
    public BaseResponse<MenusInMealResponse> getMenusInMeal(@PathVariable("mealId") Long mealId) {
        return BaseResponse.success(mealService.getMenusInMealByMealId(mealId));
    }
}
