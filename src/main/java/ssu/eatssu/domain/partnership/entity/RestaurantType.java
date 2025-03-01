package ssu.eatssu.domain.partnership.entity;

public enum RestaurantType {
    RESTAURANT("음식점"),
    CAFE("카페");

    private final String type;
    ;

    RestaurantType(String type) {
        this.type = type;
    }
}
