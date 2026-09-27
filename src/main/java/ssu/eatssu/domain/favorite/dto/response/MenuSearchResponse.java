package ssu.eatssu.domain.favorite.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;

@Schema(title = "찜 메뉴 검색 결과")
public record MenuSearchResponse(
        @Schema(description = "메뉴 id", example = "1")
        Long menuId,
        @Schema(description = "메뉴명(사용자 언어, 번역이 없으면 한국어)", example = "돈까스")
        String menuName,
        @Schema(description = "식당", example = "HAKSIK")
        Restaurant restaurant,
        @Schema(description = "찜 여부", example = "true")
        boolean isFavorite
) {

    public static MenuSearchResponse of(Menu menu, Language language, boolean isFavorite) {
        return new MenuSearchResponse(menu.getId(), menu.getNameByLanguage(language), menu.getRestaurant(),
                                      isFavorite);
    }
}
