package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@Schema(title = "리뷰에 포함되는 메뉴")
@AllArgsConstructor
public class ValidMenuForViewResponse {
    @Schema(description = "리뷰에 포함되는 메뉴 리스트", example = "[김치볶음밥, 고구마치즈돈까스, 김자반]")
    private List<MenuDto> menuList;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MenuDto {
        @Schema(description = "메뉴 ID", example = "3143")
        private Long menuId;

        @Schema(description = "메뉴 이름", example = "우삼겹갈비탕")
        private String name;
    }

}
