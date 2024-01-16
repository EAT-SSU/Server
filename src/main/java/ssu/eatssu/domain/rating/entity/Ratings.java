package ssu.eatssu.domain.rating.entity;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Ratings {

    private Integer mainRating;

    private Integer amountRating;

    private Integer tasteRating;

    private Ratings(Integer mainRating, Integer amountRating, Integer tasteRating) {
        Assert.isTrue(mainRating >= 0 && mainRating <= 5, "mainRating must be between 0 and 5");
        Assert.isTrue(amountRating >= 0 && amountRating <= 5, "amountRating must be between 0 and 5");
        Assert.isTrue(tasteRating >= 0 && tasteRating <= 5, "tasteRating must be between 0 and 5");
        this.mainRating = mainRating;
        this.amountRating = amountRating;
        this.tasteRating = tasteRating;
    }

    public static Ratings of(Integer mainRating, Integer amountRating, Integer tasteRating) {
        return new Ratings(mainRating, amountRating, tasteRating);
    }
}
