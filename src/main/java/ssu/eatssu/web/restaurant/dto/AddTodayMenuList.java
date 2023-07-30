package ssu.eatssu.web.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(title = "식단 추가")
@NoArgsConstructor
@Getter
public class AddTodayMenuList {

    private List<AddTodayMenu> addTodayMenuList;

    @NoArgsConstructor
    @Getter
    public static class AddTodayMenu{
        @Schema(description = "메뉴 이름", example = "돈까스")
        private String name;

        @Schema(description = "메뉴 가격", example = "5000")
        private Integer price;
    }
}
