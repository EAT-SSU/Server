package ssu.eatssu.domain.menu.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(title = "카테고리 별 메뉴 목록 Response")
public class CategoryWithMenusResponse {

	@Schema(description = "카테고리명")
	private final String category;

	@Schema(description = "카테고리의 메뉴 목록")
	private final List<MenuResponse> menus;

	public CategoryWithMenusResponse(String category, List<MenuResponse> menus) {
		this.category = category;
		this.menus = menus;
	}

	public static CategoryWithMenusResponse of(String category, List<MenuResponse> menus) {
		return new CategoryWithMenusResponse(category, menus);
	}
}
