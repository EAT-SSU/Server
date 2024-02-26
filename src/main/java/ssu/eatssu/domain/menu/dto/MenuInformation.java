package ssu.eatssu.domain.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.menu.entity.Menu;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "고정 메뉴 정보")
public class MenuInformation {
    @Schema(description = "메뉴 식별자", example = "2")
    private Long menuId;

    @Schema(description = "메뉴 이름", example = "돈까스")
    private String name;

    @Schema(description = "가격", example = "5000")
    private Integer price;

    @Schema(description = "메뉴 평점(평점이 없으면 null)", example = "4.4")
    private Double mainRating;

    public static MenuInformation from(Menu menu) {
        return new MenuInformation(menu.getId(), menu.getName(),
                menu.getPrice(), menu.getReviews().getAverageMainRating());
    }
}
