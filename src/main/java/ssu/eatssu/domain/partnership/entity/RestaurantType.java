package ssu.eatssu.domain.partnership.entity;

public enum RestaurantType {
    RESTAURANT("음식점"),
    CAFE("카페"),
    BEER("주점");

    private final String type;

    RestaurantType(String type) {
        this.type = type;
    }
}
