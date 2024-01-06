package ssu.eatssu.domain.review;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class Rates {

    Integer mainRate;

    Integer amountRate;

    Integer tasteRate;

}
