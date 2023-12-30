package ssu.eatssu.web.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.OpenHour;
import ssu.eatssu.domain.Restaurant;

import java.util.ArrayList;
import java.util.List;

@Schema(title = "식당 정보")
@AllArgsConstructor
@Getter
public class RestaurantInfo {

    @Schema(description = "식당위치", example = "기숙사 1층")
    private String location;

    @Schema(description = "식당 운영시간 리스트")
    private List<OpenHourInfo> openHours;

    @AllArgsConstructor
    @Getter
    private static class OpenHourInfo {
        @Schema(description = "평일/주말", example = "주중")
        private String dayType;

        @Schema(description = "아침/점심/저녁", example = "점심")
        private String timepart;

        @Schema(description = "운영 시간", example = "11:00 - 14:00")
        private String time;

        private static OpenHourInfo from(OpenHour openHour) {
            return new OpenHourInfo(openHour.getDayType().getKrName(),openHour.getTimePart().getKrName(), openHour.getTime());
        }
    }

    public static RestaurantInfo from (Restaurant restaurant){
        List<OpenHourInfo> openHours = new ArrayList<>();
        restaurant.getOpenHours().forEach(openHour -> openHours.add(OpenHourInfo.from(openHour)));
        return new RestaurantInfo(restaurant.getLocation(),openHours);
    }
}
