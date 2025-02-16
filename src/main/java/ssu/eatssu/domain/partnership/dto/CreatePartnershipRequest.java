package ssu.eatssu.domain.partnership.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipType;
import ssu.eatssu.domain.partnership.entity.RestaurantType;

@Getter
@AllArgsConstructor
public class CreatePartnershipRequest {
    private String storeName;
    private PartnershipType partnershipType;
    private String targetType; // college or department
    private String targetName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private RestaurantType restaurantType;
    private Double longitude;
    private Double latitude;

    public Partnership toPartnershipEntity() {
        return Partnership.builder()
                .partnershipType(partnershipType)
                .storeName(storeName)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .restaurantType(restaurantType)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }
}
