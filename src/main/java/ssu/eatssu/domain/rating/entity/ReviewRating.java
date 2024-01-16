package ssu.eatssu.domain.rating.entity;

import ssu.eatssu.domain.review.dto.ReviewRatingCount;

public enum ReviewRating {
    RATING_ONE(1),
    RATING_TWO(2),
    RATING_THREE(3),
    RATING_FOUR(4),
    RATING_FIVE(5);

    private final int value;
    private int count;

    ReviewRating(int value) {
        this.value = value;
        this.count = 0;
    }

    public static void resetAll() {
        for (ReviewRating RATING : ReviewRating.values()) {
            RATING.resetCount();
        }
    }

    public static ReviewRating fromValue(int value) {
        for (ReviewRating RATING : ReviewRating.values()) {
            if (RATING.value == value) {
	return RATING;
            }
        }
        throw new IllegalArgumentException("Invalid ratings value: " + value);
    }

    public static ReviewRatingCount toResponse() {
        return new ReviewRatingCount(
            RATING_ONE.getCount(),
            RATING_TWO.getCount(),
            RATING_THREE.getCount(),
            RATING_FOUR.getCount(),
            RATING_FIVE.getCount()
        );
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
}
