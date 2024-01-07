package ssu.eatssu.domain.rate;

import ssu.eatssu.web.review.dto.ReviewRateResponse;

public enum ReviewRate {
    RATE_1(1),
    RATE_2(2),
    RATE_3(3),
    RATE_4(4),
    RATE_5(5);

    private final int value;
    private int count;

    ReviewRate(int value) {
        this.value = value;
        this.count = 0;
    }

    public void incrementCount() {
        this.count++;
    }

    public int getCount() {
        return count;
    }

    private void resetCount() {
        this.count = 0;
    }

    public static void resetAll() {
        for (ReviewRate rate : ReviewRate.values()) {
            rate.resetCount();
        }
    }

    public static ReviewRate fromValue(int value) {
        for (ReviewRate rate : ReviewRate.values()) {
            if (rate.value == value) {
	return rate;
            }
        }
        throw new IllegalArgumentException("Invalid rate value: " + value);
    }

    public static ReviewRateResponse toResponse() {
        return new ReviewRateResponse(
            RATE_1.getCount(),
            RATE_2.getCount(),
            RATE_3.getCount(),
            RATE_4.getCount(),
            RATE_5.getCount()
        );
    }
}
