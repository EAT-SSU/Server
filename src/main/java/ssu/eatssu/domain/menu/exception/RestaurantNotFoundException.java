package ssu.eatssu.domain.menu.exception;

public class RestaurantNotFoundException extends IllegalArgumentException {

    public RestaurantNotFoundException() {
        super("해당 식당이 존재하지 않습니다.");
    }

    public RestaurantNotFoundException(String s) {
        super(s);
    }

    public RestaurantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
