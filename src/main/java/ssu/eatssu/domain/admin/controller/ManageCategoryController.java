package ssu.eatssu.domain.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
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
