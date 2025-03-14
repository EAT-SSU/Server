package ssu.eatssu.domain.admin.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ssu.eatssu.domain.admin.dto.RegisterCategoryRequest;
import ssu.eatssu.domain.admin.service.ManageCategoryService;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.global.handler.response.BaseResponse;

@Controller
@RequestMapping("/admin/menu/category")
@RequiredArgsConstructor
public class ManageCategoryController {

	private final ManageCategoryService manageCategoryService;

	@ResponseBody
	@PostMapping("/")
	public BaseResponse register(@RequestParam Restaurant restaurant,
		@RequestBody RegisterCategoryRequest request) {
		manageCategoryService.register(restaurant, request);
		return BaseResponse.success();
	}
}
