package ssu.eatssu.utils.validators;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.enums.TimePart;
import ssu.eatssu.domain.menu.Meal;
import ssu.eatssu.domain.restaurant.Restaurant;
import ssu.eatssu.web.menu.dto.MenuReqDto;

@RequiredArgsConstructor
public class MenuValidator {

    public static boolean validateExistedMeal(List<Meal> meals, TimePart timePart, String date,
        Restaurant restaurant,
        MenuReqDto.AddTodayMenuList addTodayMenuList) {

        //비교를 위한 정렬
        Collections.sort(addTodayMenuList.getTodayMenuList());

        //식단 목록을 돌면서 메뉴 목록을 정렬하고 비교
        for (Meal meal : meals) {
            List<String> menuNameList = meal.getMenuNames();
            Collections.sort(menuNameList);

            //메뉴 목록이 같다면 이미 존재하는 식단
            if (menuNameList.equals(addTodayMenuList.getTodayMenuList())) {
	return true;
            }
        }

        return false;
    }
}
