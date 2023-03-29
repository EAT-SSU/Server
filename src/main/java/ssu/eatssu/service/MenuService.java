package ssu.eatssu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.Restaurant;
import ssu.eatssu.domain.TodayMenu;
import ssu.eatssu.domain.enums.RestaurantName;
import ssu.eatssu.domain.enums.TimePart;
import ssu.eatssu.domain.repository.MenuRepository;
import ssu.eatssu.domain.repository.RestaurantRepository;
import ssu.eatssu.domain.repository.TodayMenuRepository;
import ssu.eatssu.web.restaurant.dto.AddTodayMenuList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final TodayMenuRepository todayMenuRepository;
    private final RestaurantRepository restaurantRepository;

    public List<Menu> findFixMenuByRestaurant(RestaurantName restaurantName) {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        return menuRepository.findAllByRestaurant(restaurant);
    }

    public List<Menu> findMenuByTimePart(TimePart timePart, String date, RestaurantName restaurantName) throws ParseException {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        //date format 맞추기
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date formatDate = simpleDateFormat.parse(date);

        List<TodayMenu> todayMenus = todayMenuRepository.findAllByDateAndTimePartAndRestaurant(formatDate, timePart,
                restaurant);
        return todayMenus.stream().map(TodayMenu::getMenu).toList();
    }

    public void addTodayMenuByTimePart(TimePart timePart, String date, RestaurantName restaurantName,
                                       AddTodayMenuList addTodayMenuList) throws ParseException {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        //date format 맞추기
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date formatDate = simpleDateFormat.parse(date);

        for (AddTodayMenuList.AddTodayMenu addTodayMenu : addTodayMenuList.getAddTodayMenuList()) {
            //메뉴 체크 (기존에 없던 새로운 메뉴 라면)
            if(!menuRepository.existsByNameAndRestaurant(addTodayMenu.getName(), restaurant)) {
                //메뉴 추가
                menuRepository.save(Menu.addFixPrice(addTodayMenu.getName(), restaurant));
            }
            //메뉴 찾아서
            Menu menu = menuRepository.findByNameAndRestaurant(addTodayMenu.getName(), restaurant)
                    .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
            //오늘의 메뉴 추가
            todayMenuRepository.save(TodayMenu.builder().date(formatDate).timePart(timePart).restaurant(restaurant)
                    .menu(menu).build());
        }
    }


}
