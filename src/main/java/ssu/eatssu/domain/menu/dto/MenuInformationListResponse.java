package ssu.eatssu.domain.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(title = "고정 메뉴 목록 조회")
public class MenuInformationListResponse {
    @Schema(description = "고정 메뉴 목록")
    private List<MenuInformation> menusInformationList;
}
