package ssu.eatssu.domain.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Schema(title = "카테고리와 메뉴 목록 조회")
public class CategoryMenuList {
    @Schema(description = "카테고리명")
    private final String category;

    @Schema(description = "카테고리의 메뉴 목록")
    private final List<MenuInformation> menuInformationList;
    
    public CategoryMenuList(String category, List<MenuInformation> menuInformationList) {
        this.category = category;
        this.menuInformationList = menuInformationList;
    }
}
