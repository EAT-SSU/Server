package ssu.eatssu.domain.admin.dto;

import java.util.List;

public record RegisterMealRequest(List<String> menuNames) {
    public RegisterMealRequest {
        menuNames.sort(String::compareTo);
    }
}
