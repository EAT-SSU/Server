package ssu.eatssu.domain.partnership.dto.request;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CreatePartnershipRequestTest {

    @Test
    void exposesAllRequestFields() {
        LocalDate startDate = LocalDate.of(2026, 3, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 30);
        CreatePartnershipRequest request = new CreatePartnershipRequest(1L, "IT대", "컴퓨터학부", "10% 할인",
                                                                         startDate, endDate);

        assertThat(request.getStoreId()).isEqualTo(1L);
        assertThat(request.getCollege()).isEqualTo("IT대");
        assertThat(request.getDepartment()).isEqualTo("컴퓨터학부");
        assertThat(request.getDescription()).isEqualTo("10% 할인");
        assertThat(request.getStartDate()).isEqualTo(startDate);
        assertThat(request.getEndDate()).isEqualTo(endDate);
    }

    @Test
    void toPartnershipEntityBuildsPartnershipLinkedToRestaurant() {
        LocalDate startDate = LocalDate.of(2026, 3, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 30);
        PartnershipRestaurant restaurant = mock(PartnershipRestaurant.class);
        CreatePartnershipRequest request = new CreatePartnershipRequest(1L, "IT대", "컴퓨터학부", "10% 할인",
                                                                         startDate, endDate);

        Partnership partnership = request.toPartnershipEntity(restaurant);

        assertThat(partnership.getPartnershipRestaurant()).isSameAs(restaurant);
        assertThat(partnership.getDescription()).isEqualTo("10% 할인");
        assertThat(partnership.getStartDate()).isEqualTo(startDate);
        assertThat(partnership.getEndDate()).isEqualTo(endDate);
    }
}
