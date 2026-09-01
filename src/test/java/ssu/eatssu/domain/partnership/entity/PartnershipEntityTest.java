package ssu.eatssu.domain.partnership.entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.user.entity.Language;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PartnershipEntityTest {

    @Test
    void partnershipDescriptionFallsBackToKoreanTranslation() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);
        PartnershipRestaurant restaurant = new PartnershipRestaurant();
        Partnership partnership = Partnership.builder().description("한국어 설명").descriptionEn("English description")
                .descriptionJa(null).descriptionVi("베트남어 설명").startDate(startDate)
                .endDate(endDate).periodType(PeriodType.NORMAL).partnershipRestaurant(restaurant).build();

        assertThat(partnership.getDescriptionByLanguage(Language.EN)).isEqualTo("English description");
        assertThat(partnership.getDescriptionByLanguage(Language.JA)).isEqualTo("한국어 설명");
        assertThat(partnership.getDescriptionByLanguage(null)).isEqualTo("한국어 설명");

        assertThat(partnership.getDescription()).isEqualTo("한국어 설명");
        assertThat(partnership.getDescriptionEn()).isEqualTo("English description");
        assertThat(partnership.getDescriptionJa()).isNull();
        assertThat(partnership.getDescriptionVi()).isEqualTo("베트남어 설명");
        assertThat(partnership.getStartDate()).isEqualTo(startDate);
        assertThat(partnership.getEndDate()).isEqualTo(endDate);
        assertThat(partnership.getPeriodType()).isEqualTo(PeriodType.NORMAL);
        assertThat(partnership.getPartnershipRestaurant()).isSameAs(restaurant);
        assertThat(partnership.getId()).isNull();
        assertThat(partnership.getPartnershipCollege()).isNull();
        assertThat(partnership.getPartnershipDepartment()).isNull();
    }

    @Test
    void partnershipRestaurantUsesLocalizedStoreName() {
        PartnershipRestaurant restaurant = new PartnershipRestaurant();
        ReflectionTestUtils.setField(restaurant, "storeNameKo", "한국 가게");
        ReflectionTestUtils.setField(restaurant, "storeNameEn", "Korean Store");

        assertThat(restaurant.getStoreNameByLanguage(Language.EN)).isEqualTo("Korean Store");
        assertThat(restaurant.getStoreNameByLanguage(Language.VI)).isEqualTo("한국 가게");
        assertThat(restaurant.getStoreName()).isEqualTo("한국 가게");
    }
}
