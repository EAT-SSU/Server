package ssu.eatssu.domain.partnership.presentation;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.partnership.service.PartnershipService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PartnershipControllerTest {

    @Test
    void 제휴_목록을_반환한다() {
        PartnershipService partnershipService = mock(PartnershipService.class);
        when(partnershipService.getAllPartnerships(null)).thenReturn(java.util.List.of());
        PartnershipController controller = new PartnershipController(partnershipService);

        assertThat(controller.getAllPartnerships(null).getResult()).isEmpty();
    }
}
