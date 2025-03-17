package ssu.eatssu.domain.partnership.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipType;
import ssu.eatssu.domain.partnership.entity.RestaurantType;

@Getter
@AllArgsConstructor
public class PartnershipResponse {
	private Long id;
	private PartnershipType partnershipType;
	private String storeName;
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;
	private RestaurantType restaurantType;
	private Double longitude;
	private Double latitude;
	private List<String> collegeNames;
	private List<String> departmentNames;

	public static PartnershipResponse fromEntity(Partnership partnership) {
		List<String> collegeNames = partnership.getPartnershipColleges().stream()
											   .map(pc -> pc.getCollege().getName())
											   .collect(Collectors.toList());
		List<String> departmentNames = partnership.getPartnershipDepartments().stream()
												  .map(pc -> pc.getDepartment().getName())
												  .collect(Collectors.toList());

		return new PartnershipResponse(
			partnership.getId(),
			partnership.getPartnershipType(),
			partnership.getStoreName(),
			partnership.getDescription(),
			partnership.getStartDate(),
			partnership.getEndDate(),
			partnership.getRestaurantType(),
			partnership.getLongitude(),
			partnership.getLatitude(),
			collegeNames,
			departmentNames
		);
	}
}
