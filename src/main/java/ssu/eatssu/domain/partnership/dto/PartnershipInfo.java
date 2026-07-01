package ssu.eatssu.domain.partnership.dto;

import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.partnership.entity.PeriodType;
import ssu.eatssu.domain.user.entity.Language;

import java.time.LocalDate;

@Builder
@Getter
public class PartnershipInfo {
    private Long id;
    private String collegeName;
    private String departmentName;
    private Integer likeCount;
    private Boolean isLiked;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private PeriodType periodType;

    public static PartnershipInfo fromEntity(Partnership partnership,
                                             PartnershipRestaurant restaurant,
                                             boolean isLiked,
                                             Language language) {
        return PartnershipInfo.builder()
                              .id(partnership.getId())
                              .description(partnership.getDescriptionByLanguage(language))
                              .startDate(partnership.getStartDate())
                              .endDate(partnership.getEndDate())
                              .periodType(partnership.getPeriodType())
                              .collegeName(partnership.getPartnershipCollege() == null && partnership.getPartnershipDepartment() == null
                                                   ? "총학생회"
                                                   : (partnership.getPartnershipCollege() != null ? partnership.getPartnershipCollege()
                                                                                                               .getName() : null))
                              .departmentName(partnership.getPartnershipDepartment() != null ? partnership.getPartnershipDepartment()
                                                                                                          .getName() : null)
                              .likeCount(restaurant.getLikes() != null ? restaurant.getLikes().size() : 0)
                              .isLiked(isLiked)
                              .build();
    }
}
