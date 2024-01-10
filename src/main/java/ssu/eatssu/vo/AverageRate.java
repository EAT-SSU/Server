package ssu.eatssu.vo;

import lombok.Builder;

public record AverageRate(Double mainRate, Double amountRate, Double tasteRate) {
    @Builder
    public AverageRate {
    }
}
