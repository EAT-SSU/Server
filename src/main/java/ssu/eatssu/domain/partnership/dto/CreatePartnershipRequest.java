package ssu.eatssu.domain.partnership.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.partnership.entity.PartnershipType;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;

import java.time.LocalDate;

@Schema(title = "제휴 등록")
@Getter
@AllArgsConstructor
public class CreatePartnershipRequest {
    @Schema(description = "제휴 가게 식별자", example = "1")
    private Long storeId;
    @Schema(description = "제휴 종류(DRINK, DISCOUNT, SIDE, OTHER)", example = "DISCOUNT")
    private PartnershipType partnershipType;
    @Schema(description = "단과대", example = "IT대")
    private String college;
    @Schema(description = "학과", example = "컴퓨터학부")
    private String department;
    @Schema(description = "제휴 내용", example = "IT대 학생증은 10% 할인")
    private String description;
    @Schema(description = "제휴 시작 날짜", example = "2025-03-01")
    private LocalDate startDate;
    @Schema(description = "제휴 종료 날짜", example = "2025-07-30")
    private LocalDate endDate;
    public Partnership toPartnershipEntity(PartnershipRestaurant partnershipRestaurant) {
        return Partnership.builder()
                          .partnershipRestaurant(partnershipRestaurant)
                          .partnershipType(partnershipType)
                          .description(description)
                          .startDate(startDate)
                          .endDate(endDate)
                          .build();
    }
}
