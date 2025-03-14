package ssu.eatssu.domain.menu.presentation.dto.response;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MenuRestaurantResponse {

	private final List<CategoryWithMenusResponse> categoryMenuListCollection;

	private MenuRestaurantResponse(List<CategoryWithMenusResponse> categoryMenu) {
		this.categoryMenuListCollection = categoryMenu;
	}

	public static MenuRestaurantResponse init() {
		return new MenuRestaurantResponse(new ArrayList<>());
	}

	public void add(CategoryWithMenusResponse categoryWithMenusResponse) {
		this.categoryMenuListCollection.add(categoryWithMenusResponse);
	}

	public void addAll(List<CategoryWithMenusResponse> categoryWithMenusResponses) {
		this.categoryMenuListCollection.addAll(categoryWithMenusResponses);
	}
}
