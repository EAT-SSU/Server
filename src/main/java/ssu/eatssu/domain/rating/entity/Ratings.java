package ssu.eatssu.domain.rating.entity;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Ratings {

    @Column(name = "main_rating")
    private Integer mainRating;

    @Column(name = "amount_rating")
    private Integer amountRating;

    @Column(name = "taste_rating")
    private Integer tasteRating;

    private Ratings(Integer mainRating, Integer amountRating, Integer tasteRating) {
        validateRange(mainRating, "mainRating");
        validateRange(amountRating, "amountRating");
        validateRange(tasteRating, "tasteRating");
        this.mainRating = mainRating;
        this.amountRating = amountRating;
        this.tasteRating = tasteRating;
    }

    private static void validateRange(Integer rating, String fieldName) {
        if (rating == null) {
            return;
        }
        Assert.isTrue(rating >= 0 && rating <= 5, fieldName + " must be between 0 and 5");
    }

    public static Ratings of(Integer mainRating, Integer amountRating, Integer tasteRating) {
        return new Ratings(mainRating, amountRating, tasteRating);
    }
}
