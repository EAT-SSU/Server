package ssu.eatssu.web.restaurant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.enums.MenuTypeGroup;
import ssu.eatssu.domain.enums.RestaurantName;
import ssu.eatssu.domain.enums.TimePart;
import ssu.eatssu.service.MenuService;
import ssu.eatssu.web.restaurant.dto.AddTodayMenuList;
import ssu.eatssu.web.restaurant.dto.TodayMenu;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Tag(name="Menu",description = "메뉴 API")
public class MenuController {

    private final MenuService menuService;

    /**
     * 고정메뉴 파는거 조회
     */
    @Operation(summary = "고정메뉴 파는거 조회", description = "고정메뉴 파는거 조회")
    @GetMapping("")
    public ResponseEntity<TodayMenu> morningMenuList(@Parameter(description = "식당이름")@RequestParam("restaurant")
                                                             RestaurantName restaurantName){
        if(!MenuTypeGroup.isFix(restaurantName))
            throw new IllegalArgumentException("no fix menu restaurant!");
        List<Menu> menus = menuService.findFixMenuByRestaurant(restaurantName);
        TodayMenu todayMenu = TodayMenu.from(menus);
        return ResponseEntity.ok(todayMenu);
    }

    /**
     * 특정 식당의 아침메뉴 조회
     */
    @Operation(summary = "특정 식당의 아침메뉴 조회", description = "특정 식당의 아침메뉴 조회")
    @GetMapping("/{date}/morning")
    public ResponseEntity<TodayMenu> morningMenuList(@Parameter(description = "날짜(yyyyMMdd)") @PathVariable("date") String date,
                                                    @Parameter(description = "식당이름")@RequestParam("restaurant")
                                                   RestaurantName restaurantName) throws ParseException {
        if(!MenuTypeGroup.isChange(restaurantName))
            throw new IllegalArgumentException("no change menu restaurant!");
        List<Menu> menus = menuService.findMenuByTimePart(TimePart.MORNING, date, restaurantName);
        TodayMenu todayMenu = TodayMenu.from(menus);
        return ResponseEntity.ok(todayMenu);
    }

    /**
     * 특정 식당의 점심메뉴 조회
     */
    @Operation(summary = "특정 식당의 점심메뉴 조회", description = "특정 식당의 점심메뉴 조회")
    @GetMapping("/{date}/lunch")
    public ResponseEntity<TodayMenu> lunchMenuList(@Parameter(description = "날짜(yyyyMMdd)") @PathVariable("date") String date,
                                                     @Parameter(description = "식당이름")@RequestParam("restaurant")
                                                             RestaurantName restaurantName) throws ParseException {
        List<Menu> menus = new ArrayList<>();
        if(MenuTypeGroup.isChange(restaurantName)){
            menus = menuService.findMenuByTimePart(TimePart.LUNCH, date, restaurantName);
        }else if(MenuTypeGroup.isFix(restaurantName)){
            menus = menuService.findFixMenuByRestaurant(restaurantName);
        }else{
            throw new IllegalArgumentException("not fount restaurant!");
        }
        TodayMenu todayMenu = TodayMenu.from(menus);
        return ResponseEntity.ok(todayMenu);
    }

    /**
     * 특정 식당의 저녁메뉴 조회
     */
    @Operation(summary = "특정 식당의 저녁메뉴 조회", description = "특정 식당의 저녁메뉴 조회")
    @GetMapping("/{date}/dinner")
    public ResponseEntity<TodayMenu> dinnerMenuList(@Parameter(description = "날짜(yyyyMMdd)") @PathVariable("date") String date,
                                                     @Parameter(description = "식당이름")@RequestParam("restaurant")
                                                             RestaurantName restaurantName) throws ParseException {

        if(!MenuTypeGroup.isChange(restaurantName))
            throw new IllegalArgumentException("no change menu restaurant!");
        List<Menu> menus = menuService.findMenuByTimePart(TimePart.DINNER, date, restaurantName);
        TodayMenu todayMenu = TodayMenu.from(menus);
        return ResponseEntity.ok(todayMenu);
    }


    /**
     * 특정 식당의 특정 날짜 아침메뉴 추가하기
     */
    @Operation(summary = "특정 식당의 아침메뉴 추가", description = "특정 식당의 아침메뉴 추가")
    @PostMapping("/{date}/morning")
    public ResponseEntity morningMenuAdd(@Parameter(description = "날짜(yyyyMMdd)") @PathVariable("date") String date,
                                                    @Parameter(description = "식당이름")@RequestParam("restaurant")
                                                            RestaurantName restaurantName,
                                                    @RequestBody AddTodayMenuList addTodayMenuList) throws ParseException {

        if(!MenuTypeGroup.isChange(restaurantName))
            throw new IllegalArgumentException("no change menu restaurant!");
        menuService.addTodayMenuByTimePart(TimePart.MORNING, date, restaurantName, addTodayMenuList);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 특정 식당의 특정 날짜 점심메뉴 추가하기
     */
    @Operation(summary = "특정 식당의 점심메뉴 추가", description = "특정 식당의 점심메뉴 추가")
    @PostMapping("/{date}/lunch")
    public ResponseEntity lunchMenuAdd(@Parameter(description = "날짜(yyyyMMdd)") @PathVariable("date") String date,
                                         @Parameter(description = "식당이름")@RequestParam("restaurant")
                                                 RestaurantName restaurantName,
                                         @RequestBody AddTodayMenuList addTodayMenuList) throws ParseException {

        if(!MenuTypeGroup.isChange(restaurantName))
            throw new IllegalArgumentException("no change menu restaurant!");
        menuService.addTodayMenuByTimePart(TimePart.LUNCH, date, restaurantName, addTodayMenuList);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 특정 식당의 특정 날짜 저녁메뉴 추가하기
     */
    @Operation(summary = "특정 식당의 저녁메뉴 추가", description = "특정 식당의 저녁메뉴 추가")
    @PostMapping("/{date}/dinner")
    public ResponseEntity dinnerMenuAdd(@Parameter(description = "날짜(yyyyMMdd)") @PathVariable("date") String date,
                                         @Parameter(description = "식당이름")@RequestParam("restaurant")
                                                 RestaurantName restaurantName,
                                         @RequestBody AddTodayMenuList addTodayMenuList) throws ParseException {

        if(!MenuTypeGroup.isChange(restaurantName))
            throw new IllegalArgumentException("no change menu restaurant!");
        menuService.addTodayMenuByTimePart(TimePart.DINNER, date, restaurantName, addTodayMenuList);
        return new ResponseEntity(HttpStatus.OK);
    }

}
