package ssu.eatssu.domain.partnership.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PartnershipQuerydslTest {

    @Test
    void partnershipQueryExposesEntityFields() {
        QPartnership query = QPartnership.partnership;

        assertThat(query.getType()).isEqualTo(Partnership.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.description.getMetadata().getName()).isEqualTo("description");
        assertThat(query.startDate.getMetadata().getName()).isEqualTo("startDate");
        assertThat(query.endDate.getMetadata().getName()).isEqualTo("endDate");
        assertThat(query.periodType.getMetadata().getName()).isEqualTo("periodType");
    }

    @Test
    void partnershipLikeQueryExposesRelations() {
        QPartnershipLike query = QPartnershipLike.partnershipLike;

        assertThat(query.getType()).isEqualTo(PartnershipLike.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.user.getMetadata().getName()).isEqualTo("user");
        assertThat(query.partnershipRestaurant.getMetadata().getName()).isEqualTo("partnershipRestaurant");
    }

    @Test
    void partnershipRestaurantQueryExposesCollectionsAndLocalizedNames() {
        QPartnershipRestaurant query = QPartnershipRestaurant.partnershipRestaurant;

        assertThat(query.getType()).isEqualTo(PartnershipRestaurant.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.storeNameKo.getMetadata().getName()).isEqualTo("storeNameKo");
        assertThat(query.storeNameEn.getMetadata().getName()).isEqualTo("storeNameEn");
        assertThat(query.restaurantType.getMetadata().getName()).isEqualTo("restaurantType");
        assertThat(query.likes.getMetadata().getName()).isEqualTo("likes");
        assertThat(query.partnerships.getMetadata().getName()).isEqualTo("partnerships");
    }
}
