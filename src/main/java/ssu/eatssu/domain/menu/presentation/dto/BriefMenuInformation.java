package ssu.eatssu.domain.menu.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.menu.entity.Menu;

@Getter
@NoArgsConstructor
@Schema(title = "메뉴 간단 설명")
public class BriefMenuInformation {

    @Schema(description = "메뉴 식별자", example = "2")
    private Long menuId;

    @Schema(description = "메뉴 이름", example = "돈까스")
    private String name;

    public BriefMenuInformation(Menu menu) {
        this.menuId = menu.getId();
        this.name = menu.getName();
    }
}
