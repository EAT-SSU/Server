package ssu.eatssu.domain.admin.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ssu.eatssu.domain.admin.dto.MealInfo;
import ssu.eatssu.domain.admin.dto.MenuBoards;
import ssu.eatssu.domain.admin.dto.RegisterMealRequest;
import ssu.eatssu.domain.admin.service.ManageMealService;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.Date;

@Controller
@RequestMapping("/admin/meals")
@RequiredArgsConstructor
public class ManageMealController {

	private final ManageMealService manageMealService;

	@ResponseBody
	@GetMapping("")
	public BaseResponse<MenuBoards> mealPage(@RequestParam @DateTimeFormat(pattern = "yyyyMMdd") Date date,
		@RequestParam TimePart timePart,
		Model model) {
		MenuBoards menuBoards = manageMealService.getMenuBoards(date, timePart);
		return BaseResponse.success(menuBoards);
	}

	@ResponseBody
	@PostMapping("")
	public BaseResponse register(@RequestParam Restaurant restaurant,
		@RequestParam @DateTimeFormat(pattern = "yyyyMMdd") Date date,
		@RequestParam TimePart timePart,
		@RequestBody RegisterMealRequest request,
		Model model) {
		MealInfo mealInfo = new MealInfo(restaurant, date, timePart);
		manageMealService.register(mealInfo, request);
		return BaseResponse.success();
	}

	@ResponseBody
	@DeleteMapping("/{mealId}")
	public BaseResponse delete(@PathVariable Long mealId) {
		manageMealService.delete(mealId);
		return BaseResponse.success();
	}

}