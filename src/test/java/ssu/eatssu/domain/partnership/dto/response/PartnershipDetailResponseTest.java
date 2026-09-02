package ssu.eatssu.domain.partnership.dto.response;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipLike;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.partnership.entity.RestaurantType;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class PartnershipDetailResponseTest {

    @Test
    void fromEntityMapsRestaurantPartnershipAndAffiliation() {
        Partnership partnership = mock(Partnership.class);
        PartnershipRestaurant restaurant = mock(PartnershipRestaurant.class);
        College college = mock(College.class);
        Department department = mock(Department.class);
        given(partnership.getId()).willReturn(12L);
        given(partnership.getStartDate()).willReturn(LocalDate.of(2026, 1, 1));
        given(partnership.getEndDate()).willReturn(LocalDate.of(2026, 12, 31));
        given(partnership.getDescription()).willReturn("10% 할인");
        given(partnership.getPartnershipCollege()).willReturn(college);
        given(partnership.getPartnershipDepartment()).willReturn(department);
        given(college.getName()).willReturn("공과대학");
        given(department.getName()).willReturn("컴퓨터학부");
        given(restaurant.getStoreName()).willReturn("학생식당");
        given(restaurant.getRestaurantType()).willReturn(RestaurantType.RESTAURANT);
        given(restaurant.getLongitude()).willReturn(127.1);
        given(restaurant.getLatitude()).willReturn(37.5);
        given(restaurant.getLikes()).willReturn(List.of(mock(PartnershipLike.class)));

        PartnershipDetailResponse response = PartnershipDetailResponse.fromEntity(restaurant, partnership, true);

        assertThat(response.getId()).isEqualTo(12L);
        assertThat(response.getStoreName()).isEqualTo("학생식당");
        assertThat(response.getDescription()).isEqualTo("10% 할인");
        assertThat(response.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(response.getEndDate()).isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(response.getRestaurantType()).isEqualTo(RestaurantType.RESTAURANT);
        assertThat(response.getLongitude()).isEqualTo(127.1);
        assertThat(response.getLatitude()).isEqualTo(37.5);
        assertThat(response.getCollegeName()).isEqualTo("공과대학");
        assertThat(response.getDepartmentName()).isEqualTo("컴퓨터학부");
        assertThat(response.getPartnershipLikeCount()).isOne();
        assertThat(response.isLikedByUser()).isTrue();
    }

    @Test
    void fromEntityLeavesAffiliationNamesNullWhenAbsent() {
        Partnership partnership = mock(Partnership.class);
        PartnershipRestaurant restaurant = mock(PartnershipRestaurant.class);
        given(partnership.getPartnershipCollege()).willReturn(null);
        given(partnership.getPartnershipDepartment()).willReturn(null);
        given(restaurant.getLikes()).willReturn(List.of());

        PartnershipDetailResponse response = PartnershipDetailResponse.fromEntity(restaurant, partnership, false);

        assertThat(response.getCollegeName()).isNull();
        assertThat(response.getDepartmentName()).isNull();
        assertThat(response.getPartnershipLikeCount()).isZero();
        assertThat(response.isLikedByUser()).isFalse();
    }
}
