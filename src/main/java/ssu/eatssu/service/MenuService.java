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
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.web.menu.dto.MenuReqDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ssu.eatssu.handler.response.BaseResponseStatus.*;
import static ssu.eatssu.web.menu.dto.MenuResDto.*;

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
    public List<Menu> findFixMenuList(RestaurantName restaurantName) {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(() -> new BaseException(NOT_FOUND_RESTAURANT));
        return menuRepository.findAllByRestaurant(restaurant);
    }

    /**
     * 식단 목록 조회
     * <p>변동메뉴 식당(학생식당, 도담, 기숙사 식당)의 특정날짜(yyyyMMdd), 특정시간대(아침/점심/저녁)에 해당하는 식단 목록을 조회한다.</p>
     */
    public List<Meal> findMealList(TimePart timePart, String date,
                                     RestaurantName restaurantName) throws ParseException {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(() -> new BaseException(NOT_FOUND_RESTAURANT));
        //date format 맞추기
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date formatDate = simpleDateFormat.parse(date);
        return mealRepository.findAllByDateAndTimePartAndRestaurant(formatDate, timePart, restaurant);
    }

    /**
     * 식단 등록
     */
    public void createMeal(TimePart timePart, String date, RestaurantName restaurantName,
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
        newMeal.caculateRate();
    }

    /**
     * 이미 존재하는 식단인지 확인
     */
    public boolean isExistMeal(TimePart timePart, String date, RestaurantName restaurantName,
                               MenuReqDto.AddTodayMenuList addTodayMenuList) throws ParseException {
        //date format 맞추기
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date formatDate = simpleDateFormat.parse(date);
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(()->new BaseException(NOT_FOUND_RESTAURANT));
        List<Meal> meals = mealRepository.findAllByDateAndTimePartAndRestaurant(formatDate,timePart,restaurant);
        Collections.sort(addTodayMenuList.getTodayMenuList());
        for(Meal meal : meals){
            List<String> menuNameList = new ArrayList<>();
            for(MealMenu mealMenu: meal.getMealMenus()){
                menuNameList.add(mealMenu.getMenu().getName());
            }
            Collections.sort(menuNameList);
            if(menuNameList.equals(addTodayMenuList.getTodayMenuList())){
                return true;
            }
        }
        return false;
    }

    /**
     * 식단 속 메뉴 목록 조회
     */
    public MenuList findMenuListInMeal(Long mealId) {
        Meal meal = mealRepository.findById(mealId).orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));
        return MenuList.from(meal);
    }

    /**
     * 식단 삭제
     */
    public List<Long> deleteMeal(Long mealId) {
        Meal meal = mealRepository.findById(mealId).orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));
        List<Long> menuIdList = new ArrayList<>();
        meal.getMealMenus().forEach(mealMenu -> menuIdList.add(mealMenu.getMenu().getId()));
        for(Iterator<MealMenu> it = meal.getMealMenus().iterator(); it.hasNext() ; )
        {
            MealMenu mealMenu = it.next();
            it.remove();
            mealMenuRepository.delete(mealMenu);
        }
        mealRepository.delete(meal);
        return menuIdList;
    }

    /**
     * 변동 메뉴 중 어떤 식단에도 포함되지 않는 메뉴를 찾아 삭제한다.
     */
    public void cleanupGarbageMenu(List<Long> menuIdList) {
        for(Long menuId : menuIdList){
            Optional<Menu> menu = menuRepository.findById(menuId);
            if(menu.isPresent()&&menu.get().getMealMenus().isEmpty()){
                menuRepository.delete(menu.get());
            }
        }
    }
}
