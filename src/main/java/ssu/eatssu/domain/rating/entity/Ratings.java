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

    private Ratings(Integer mainRating) {
        Assert.isTrue(mainRating >= 0 && mainRating <= 5, "mainRating must be between 0 and 5");
        this.mainRating = mainRating;
    }

    public static Ratings of(Integer mainRating) {
        return new Ratings(mainRating);
    }
}
