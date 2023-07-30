package ssu.eatssu.web.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MenuReqDto {

    private MenuReqDto() {}

    @Schema(title = "식단 추가")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTodayMenuList {

        @Schema(description = "메뉴명 리스트", example = "[\"돈까스\", \"샐러드\", \"김치\"]")
        private List<String> todayMenuList;

    }
}
