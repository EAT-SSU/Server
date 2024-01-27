package ssu.eatssu.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ssu.eatssu.domain.admin.dto.MenuBoards;
import ssu.eatssu.domain.admin.service.ManageMealService;
import ssu.eatssu.domain.menu.entity.TimePart;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.Date;

@Controller
@RequestMapping("/admin/meal")
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
}