package ssu.eatssu.domain.menu.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CategoryMenuListCollection {
    private final List<CategoryMenuList> categoryMenuListCollection;

    private CategoryMenuListCollection(List<CategoryMenuList> categoryMenu) {
        this.categoryMenuListCollection = categoryMenu;
    }

    public static CategoryMenuListCollection init() {
        return new CategoryMenuListCollection(new ArrayList<>());
    }

    public void add(CategoryMenuList categoryMenuList) {
        this.categoryMenuListCollection.add(categoryMenuList);
    }
}
