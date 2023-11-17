package ssu.eatssu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.Meal;
import ssu.eatssu.domain.MealMenu;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.Restaurant;
import ssu.eatssu.domain.enums.RestaurantName;
import ssu.eatssu.domain.enums.TimePart;
import ssu.eatssu.domain.repository.MealMenuRepository;
import ssu.eatssu.domain.repository.MealRepository;
import ssu.eatssu.domain.repository.MenuRepository;
import ssu.eatssu.domain.repository.RestaurantRepository;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.web.restaurant.dto.MenuReqDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ssu.eatssu.response.BaseResponseStatus.*;
import static ssu.eatssu.web.restaurant.dto.MenuResDto.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;
    private final MealMenuRepository mealMenuRepository;
    private final RestaurantRepository restaurantRepository;

    public List<Menu> findFixMenuByRestaurant(RestaurantName restaurantName) {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(() -> new BaseException(NOT_FOUND_RESTAURANT));
        return menuRepository.findAllByRestaurant(restaurant);
    }

    public List<Meal> findTodayMeals(TimePart timePart, String date,
                                     RestaurantName restaurantName) throws ParseException {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(() -> new BaseException(NOT_FOUND_RESTAURANT));
        //date format 맞추기
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date formatDate = simpleDateFormat.parse(date);
        return mealRepository.findAllByDateAndTimePartAndRestaurant(formatDate, timePart, restaurant);
    }


    public void addMeal(TimePart timePart, String date, RestaurantName restaurantName,
                        MenuReqDto.AddTodayMenuList addTodayMenuList) throws ParseException {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(() -> new BaseException(NOT_FOUND_RESTAURANT));
        //date format 맞추기
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date formatDate = simpleDateFormat.parse(date);
        Meal newMeal = Meal.builder().date(formatDate).restaurant(restaurant).timePart(timePart).build();
        mealRepository.save(newMeal);
        for (String addMenuName : addTodayMenuList.getTodayMenuList()) {
            //메뉴 체크 (기존에 없던 새로운 메뉴 라면)
            if (!menuRepository.existsByNameAndRestaurant(addMenuName, restaurant)) {
                //메뉴 추가
                menuRepository.save(Menu.addFixPrice(addMenuName, restaurant));
            }
            //메뉴 찾아서
            Menu menu = menuRepository.findByNameAndRestaurant(addMenuName, restaurant)
                    .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));
            //식단에 메뉴 추가
            MealMenu mealMenu = MealMenu.builder().menu(menu).meal(newMeal).build();
            mealMenuRepository.save(mealMenu);
        }
        newMeal.caculateGrade();
    }

    public boolean dupliicateMealCheck(TimePart timePart, String date, RestaurantName restaurantName,
                               MenuReqDto.AddTodayMenuList addTodayMenuList) throws ParseException {
        //date format 맞추기
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date formatDate = simpleDateFormat.parse(date);
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(()->new BaseException(NOT_FOUND_RESTAURANT));
        List<Meal> meals = mealRepository.findAllByDateAndTimePartAndRestaurant(formatDate,timePart,restaurant);
        Collections.sort(addTodayMenuList.getTodayMenuList());
        for(Meal meal : meals){
            List<String> menuNameList = new ArrayList<String>();
            for(MealMenu mealMenu: meal.getMealMenus()){
                menuNameList.add(mealMenu.getMenu().getName());
            }
            Collections.sort(menuNameList);
            if(menuNameList.equals(addTodayMenuList.getTodayMenuList())){
                return true;
            };
        }
        return false;
    }

    public MenuList findAllMenu(Long mealId) {
        Meal meal = mealRepository.findById(mealId).orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));
        return MenuList.from(meal);
    }
}
