package ssu.eatssu.domain.rate;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Rate {

    private Integer mainRate;

    private Integer amountRate;

    private Integer tasteRate;

    private Rate(Integer mainRate, Integer amountRate, Integer tasteRate) {
        Assert.isTrue(mainRate >= 0 && mainRate <= 5, "mainRate must be between 0 and 5");
        Assert.isTrue(amountRate >= 0 && amountRate <= 5, "amountRate must be between 0 and 5");
        Assert.isTrue(tasteRate >= 0 && tasteRate <= 5, "tasteRate must be between 0 and 5");
        this.mainRate = mainRate;
        this.amountRate = amountRate;
        this.tasteRate = tasteRate;
    }

    public static Rate of(Integer mainRate, Integer amountRate, Integer tasteRate) {
        return new Rate(mainRate, amountRate, tasteRate);
    }
}
