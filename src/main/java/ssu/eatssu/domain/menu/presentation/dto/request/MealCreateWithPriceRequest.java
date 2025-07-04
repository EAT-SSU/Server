package ssu.eatssu.domain.menu.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "식단 추가(가격 입력 받는 버전)")
public record MealCreateWithPriceRequest(
        @Schema(description = "메뉴명 리스트", example = "[\"돈까스\", \"샐러드\", \"김치\"]")
        List<String> menuNames,
        @Schema(description = "식단 가격", example = "3000")
        Integer price
) {

}
