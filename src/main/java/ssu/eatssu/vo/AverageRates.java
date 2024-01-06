package ssu.eatssu.vo;

import lombok.Builder;

public record AverageRates(Double mainRate, Double amountRate, Double tasteRate) {
    @Builder
    public AverageRates {
    }
}
