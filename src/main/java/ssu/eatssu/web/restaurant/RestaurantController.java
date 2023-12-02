package ssu.eatssu.web.restaurant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.Restaurant;
import ssu.eatssu.domain.enums.RestaurantName;
import ssu.eatssu.domain.repository.RestaurantRepository;
import ssu.eatssu.response.BaseResponse;
import ssu.eatssu.web.restaurant.dto.RestaurantInfo;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Tag(name="Restaurant",description = "레스토랑 API")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    /**
     * 식당 위치, 운영 시간 조회
     */
    @Operation(summary = "식당 정보 조회", description = "식당 위치, 운영시간 조회")
    @GetMapping("/{restaurantName}")
    public BaseResponse<RestaurantInfo> restaurantInfo(@Parameter(description = "식당이름")@PathVariable("restaurantName")
                                                                     RestaurantName restaurantName) {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 식당이 없습니다."));
        return new BaseResponse<>(RestaurantInfo.from(restaurant));

    }

}
