package ssu.eatssu.domain.rating;

import ssu.eatssu.web.review.dto.ReviewRatingCount;

public enum ReviewRating {
    RATING_1(1),
    RATING_2(2),
    RATING_3(3),
    RATING_4(4),
    RATING_5(5);

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
                RATING_1.getCount(),
                RATING_2.getCount(),
                RATING_3.getCount(),
                RATING_4.getCount(),
                RATING_5.getCount()
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
