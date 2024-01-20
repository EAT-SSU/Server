package ssu.eatssu.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.admin.dto.MenuBoards;
import ssu.eatssu.domain.admin.dto.RegisterFixMenuRequest;
import ssu.eatssu.domain.admin.dto.UpdateFixMenuRequest;
import ssu.eatssu.domain.admin.service.ManageFixMenuService;
import ssu.eatssu.domain.restaurant.entity.RestaurantName;
import ssu.eatssu.global.handler.response.BaseResponse;

@Controller
@RequestMapping("/admin/menu/fix-menu")
@RequiredArgsConstructor
public class ManageFixMenuController {

    private final ManageFixMenuService manageFixMenuService;

    @ResponseBody
    @GetMapping("")
    public BaseResponse<MenuBoards> fixMenuPage(Model model) {
        MenuBoards menuBoards = manageFixMenuService.getMenuBoards();
        //model.addAttribute("menuBoards", manageFixMenuService.getMenuBoards());
        return BaseResponse.success(menuBoards);
    }

    //TODO ResponseBody 해제
    @ResponseBody
    @PostMapping("")
    public String register(@RequestParam RestaurantName restaurantName,
                           @RequestBody RegisterFixMenuRequest request) {
        manageFixMenuService.register(restaurantName, request);
        return "redirect:/admin/menu/fix-menu";
    }

    @ResponseBody
    @PatchMapping("{menuId}")
    public String update(@PathVariable Long menuId,
                         @RequestBody UpdateFixMenuRequest request) {
        manageFixMenuService.updateMenu(menuId, request);
        return "redirect:/admin/menu/fix-menu";
    }

    @ResponseBody
    @DeleteMapping("{menuId}")
    public String delete(@PathVariable Long menuId) {
        manageFixMenuService.delete(menuId);
        return "redirect:/admin/menu/fix-menu";
    }

}
