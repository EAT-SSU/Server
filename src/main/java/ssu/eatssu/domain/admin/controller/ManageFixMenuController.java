package ssu.eatssu.domain.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.admin.dto.MenuBoards;
import ssu.eatssu.domain.admin.dto.RegisterFixMenuRequest;
import ssu.eatssu.domain.admin.dto.UpdateFixMenuRequest;
import ssu.eatssu.domain.admin.service.ManageFixMenuService;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.global.handler.response.BaseResponse;

@Controller
@RequestMapping("/admin/menu/fix-menus")
@RequiredArgsConstructor
public class ManageFixMenuController {

	private final ManageFixMenuService manageFixMenuService;

	@ResponseBody
	@GetMapping("")
	public BaseResponse<MenuBoards> fixMenuPage() {
		MenuBoards menuBoards = manageFixMenuService.getMenuBoards();
		return BaseResponse.success(menuBoards);
	}

	@ResponseBody
	@PostMapping("")
	public BaseResponse register(@RequestParam Restaurant restaurant,
		@RequestBody RegisterFixMenuRequest request) {
		manageFixMenuService.register(restaurant, request);
		return BaseResponse.success();
	}

	@ResponseBody
	@PatchMapping("/{menuId}")
	public BaseResponse update(@PathVariable Long menuId,
		@RequestBody UpdateFixMenuRequest request) {
		manageFixMenuService.updateMenu(menuId, request);
		return BaseResponse.success();
	}

	@ResponseBody
	@DeleteMapping("/{menuId}")
	public BaseResponse delete(@PathVariable Long menuId) {
		manageFixMenuService.delete(menuId);
		return BaseResponse.success();
	}

	@ResponseBody
	@PatchMapping("/{menuId}/discontinued-status")
	public BaseResponse<Boolean> toggleDiscontinuedStatus(@PathVariable Long menuId) {
		return BaseResponse.success(manageFixMenuService.changeDiscontinuedStatus(menuId));
	}

}
