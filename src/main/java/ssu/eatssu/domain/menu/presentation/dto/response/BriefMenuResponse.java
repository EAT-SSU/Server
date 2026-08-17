package ssu.eatssu.domain.menu.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.menu.entity.Menu;

import java.util.Map;

@Getter
@NoArgsConstructor
@Schema(title = "메뉴 간단 설명")
public class BriefMenuResponse {

    @Schema(description = "메뉴 식별자", example = "2")
    private Long menuId;

    @Schema(description = "메뉴 이름", example = "돈까스")
    private String name;

    @Getter(AccessLevel.NONE)
    @Schema(description = "대표메뉴(영어 UI 노출 대상) 여부", example = "false")
    private boolean isMain;

    public BriefMenuResponse(Menu menu) {
        this(menu, Map.of());
    }

    public BriefMenuResponse(Menu menu, Map<String, String> mainMenuTranslations) {
        this.menuId = menu.getId();
        String nameEn = mainMenuTranslations.get(menu.getName());
        this.isMain = nameEn != null;
        this.name = isMain ? nameEn : menu.getName();
    }

    @JsonProperty("isMain")
    public boolean isMain() {
        return isMain;
    }
}
