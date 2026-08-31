package ssu.eatssu.domain.partnership.dto.response;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.entity.Language;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class PartnershipInfoTest {

    @Test
    void fromEntityUsesStudentCouncilWhenNoAffiliationExists() {
        Partnership partnership = mock(Partnership.class);
        PartnershipRestaurant restaurant = mock(PartnershipRestaurant.class);
        given(partnership.getDescriptionByLanguage(Language.KO)).willReturn("할인");
        given(restaurant.getLikes()).willReturn(null);

        PartnershipInfo response = PartnershipInfo.fromEntity(partnership, restaurant, false, Language.KO);

        assertThat(response.getCollegeName()).isEqualTo("총학생회");
        assertThat(response.getDepartmentName()).isNull();
        assertThat(response.getLikeCount()).isZero();
        assertThat(response.getIsLiked()).isFalse();
    }

    @Test
    void fromEntityMapsDepartmentAffiliation() {
        Partnership partnership = mock(Partnership.class);
        PartnershipRestaurant restaurant = mock(PartnershipRestaurant.class);
        Department department = mock(Department.class);
        given(partnership.getPartnershipDepartment()).willReturn(department);
        given(department.getName()).willReturn("컴퓨터학부");

        PartnershipInfo response = PartnershipInfo.fromEntity(partnership, restaurant, true, Language.KO);

        assertThat(response.getCollegeName()).isNull();
        assertThat(response.getDepartmentName()).isEqualTo("컴퓨터학부");
        assertThat(response.getIsLiked()).isTrue();
    }
}
