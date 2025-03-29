package ssu.eatssu.domain.partnership.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipType;
import ssu.eatssu.domain.partnership.entity.RestaurantType;

@Schema(title = "제휴 등록")
@Getter
@AllArgsConstructor
public class CreatePartnershipRequest {
	@Schema(description = "제휴 가게 이름", example = "먹돼지")
	private String storeName;
	@Schema(description = "제휴 종류(DRINK, DISCOUNT, SIDE, OTHER)", example = "DISCOUNT")
	private PartnershipType partnershipType;
	@Schema(description = "제휴 타겟 종류(college or department)", example = "college")
	private String targetType; // college or department
	@Schema(description = "제휴 타겟", example = "IT대")
	private String targetName;
	@Schema(description = "제휴 내용", example = "IT대 학생증은 10% 할인")
	private String description;
	@Schema(description = "제휴 시작 날짜", example = "2025-03-01")
	private LocalDate startDate;
	@Schema(description = "제휴 종료 날짜", example = "2025-07-30")
	private LocalDate endDate;
	@Schema(description = "제휴 가게 종류(RESTAURANT, CAFE)", example = "RESTAURANT")
	private RestaurantType restaurantType;
	@Schema(description = "경도(x축)", example = "126.9566592")
	private Double longitude;
	@Schema(description = "위도(y축)", example = "37.4949404")
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
