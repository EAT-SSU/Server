package ssu.eatssu.domain.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MenuRequest {

    private MenuRequest() {
    }

    @Schema(title = "식단 추가")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MealCreateRequest {

        @Schema(description = "메뉴명 리스트", example = "[\"돈까스\", \"샐러드\", \"김치\"]")
        private List<String> menuNames;

        private String title;
    }
}
