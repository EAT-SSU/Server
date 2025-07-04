package ssu.eatssu.domain.partnership.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.partnership.entity.PartnershipType;
import ssu.eatssu.domain.partnership.entity.RestaurantType;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PartnershipDetailResponse {
    private Long id;
    private PartnershipType partnershipType;
    private String storeName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private RestaurantType restaurantType;
    private Double longitude;
    private Double latitude;
    private String collegeName;
    private String departmentName;
    private int partnershipLikeCount;
    private boolean likedByUser;

    public static PartnershipDetailResponse fromEntity(PartnershipRestaurant partnershipRestaurant,
                                                       Partnership partnership,
                                                       boolean likedByUser) {

        return new PartnershipDetailResponse(
                partnership.getId(),
                partnership.getPartnershipType(),
                partnershipRestaurant.getStoreName(),
                partnership.getDescription(),
                partnership.getStartDate(),
                partnership.getEndDate(),
                partnershipRestaurant.getRestaurantType(),
                partnershipRestaurant.getLongitude(),
                partnershipRestaurant.getLatitude(),
                partnership.getPartnershipCollege() == null ? null : partnership.getPartnershipCollege().getName(),
                partnership.getPartnershipDepartment() == null ? null : partnership.getPartnershipDepartment()
                                                                                   .getName(),
                partnershipRestaurant.getLikes().size(),
                likedByUser
        );
    }
}
