package ssu.eatssu.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ssu.eatssu.domain.admin.service.ManageFixMenuService;

@Controller
@RequestMapping("/manage/menu/fix-menu")
@RequiredArgsConstructor
public class ManageFixMenuController {

    private final ManageFixMenuService manageFixMenuService;

    @GetMapping("")
    public String fixMenuPage(Model model) {
        model.addAttribute("menuBoards", manageFixMenuService.getMenuBoards());
        return "fix-menu";
    }

}
