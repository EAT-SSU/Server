package ssu.eatssu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.menu.Meal;
import ssu.eatssu.domain.menu.MealMenu;
import ssu.eatssu.domain.menu.Menu;
import ssu.eatssu.domain.restaurant.Restaurant;
import ssu.eatssu.domain.restaurant.RestaurantName;
import ssu.eatssu.domain.enums.TimePart;
import ssu.eatssu.domain.repository.MealMenuRepository;
import ssu.eatssu.domain.repository.MealRepository;
import ssu.eatssu.domain.repository.MenuRepository;
import ssu.eatssu.domain.repository.RestaurantRepository;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.handler.response.BaseResponseStatus;
import ssu.eatssu.utils.validators.MenuValidator;
import ssu.eatssu.web.menu.dto.MenuReqDto;

import java.util.*;

import static ssu.eatssu.utils.DateUtils.toDate;
import static ssu.eatssu.web.menu.dto.MenuResponse.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;
    private final MealMenuRepository mealMenuRepository;
    private final RestaurantRepository restaurantRepository;

    /**
     * 고정 메뉴 조회
     * <p>특정 식당에 해당하는 고정메뉴 목록을 조회합니다.</p>
     */
    public FixMenuList findFixMenuList(RestaurantName restaurantName) {

        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_RESTAURANT));

        return FixMenuList.from(menuRepository.findAllByRestaurant(restaurant));
    }

    /**
     * 식단 목록 조회
     * <p>변동메뉴 식당(학생식당, 도담, 기숙사 식당)의 특정날짜(yyyyMMdd), 특정시간대(아침/점심/저녁)에 해당하는 식단 목록을 조회한다.</p>
     */
    public List<TodayMeal> findMealList(TimePart timePart, String date,
        RestaurantName restaurantName) {

        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_RESTAURANT));

        List<Meal> mealList = mealRepository.findAllByDateAndTimePartAndRestaurant(toDate(date),
            timePart, restaurant);

        //mealList 를 TodayMeal 로 매핑 후 반환
        return mealList.stream().map(TodayMeal::from).toList();
    }


    /**
     * 식단 등록
     */
    public void createMeal(TimePart timePart, String date, RestaurantName restaurantName,
        MenuReqDto.AddTodayMenuList addTodayMenuList) {

        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_RESTAURANT));

        List<Meal> meals = mealRepository.findAllByDateAndTimePartAndRestaurant(toDate(date),
            timePart, restaurant);

        if (MenuValidator.validateExistedMeal(meals, timePart, date, restaurant,
            addTodayMenuList)) {
            return;
        }

        //식단 생성
        Meal newMeal = Meal.builder().date(toDate(date)).restaurant(restaurant).timePart(timePart)
            .build();
        mealRepository.save(newMeal);

        //식단에 메뉴 추가
        addMenu(newMeal, addTodayMenuList);
    }

    /**
     * 식단에 메뉴 추가
     */
    private void addMenu(Meal meal, MenuReqDto.AddTodayMenuList addTodayMenuList) {
        Restaurant restaurant = meal.getRestaurant();

        for (String addMenuName : addTodayMenuList.getTodayMenuList()) {
            //메뉴 체크 (기존에 없던 새로운 메뉴 라면)
            if (!menuRepository.existsByNameAndRestaurant(addMenuName, restaurant)) {
	//메뉴 추가
	menuRepository.save(Menu.createChangeMenu(addMenuName, restaurant));
            }

            //메뉴 찾아서
            Menu menu = menuRepository.findByNameAndRestaurant(addMenuName, restaurant)
	.orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MENU));

            //식단에 메뉴 추가
            MealMenu mealMenu = MealMenu.builder().menu(menu).meal(meal).build();
            mealMenuRepository.save(mealMenu);
        }
    }

    /**
     * 식단 속 메뉴 목록 조회
     */
    public MenuList findMenuListInMeal(Long mealId) {

        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEAL));

        return MenuList.from(meal);
    }

    /**
     * 식단 삭제
     */
    public void deleteMeal(Long mealId) {

        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEAL));

        //식단에 포함된 메뉴 목록 조회
        List<Menu> menuList = meal.getMealMenus().stream().map(MealMenu::getMenu).toList();

        mealRepository.delete(meal);

        //식단 삭제 -> mealMenu 삭제 반영
        mealRepository.flush();

        //고아 변동 메뉴 정리
        cleanupGarbageMenu(menuList);
    }

    /**
     * 변동 메뉴 중 어떤 식단에도 포함되지 않는 메뉴를 찾아 삭제한다.
     */
    public void cleanupGarbageMenu(List<Menu> menuList) {
        for (Menu menu : menuList) {
            if (menu.getMealMenus().isEmpty()) {
	menuRepository.delete(menu);
            }
        }
    }
}
