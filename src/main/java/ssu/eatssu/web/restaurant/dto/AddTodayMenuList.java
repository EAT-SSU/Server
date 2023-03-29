package ssu.eatssu.web.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class AddTodayMenuList {
    private List<AddTodayMenu> addTodayMenuList;

    @NoArgsConstructor
    @Getter
    public static class AddTodayMenu{
        private String name;
        private Integer price;
    }
}
