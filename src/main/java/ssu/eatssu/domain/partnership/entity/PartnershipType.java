package ssu.eatssu.domain.partnership.entity;

import lombok.Getter;

@Getter
public enum PartnershipType {
    DRINK("음료"),
    DISCOUNT("할인"),
    SIDE("사이드"),
    OTHER("기타");

    private final String type;

    PartnershipType(String type) {
        this.type = type;
    }
}
