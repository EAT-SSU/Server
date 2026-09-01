package ssu.eatssu.domain.partnership.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PartnershipQuerydslTest {

    @Test
    void partnershipQueryExposesEntityFields() {
        QPartnership query = QPartnership.partnership;

        assertThat(query.getType()).isEqualTo(Partnership.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.description.getMetadata().getName()).isEqualTo("description");
        assertThat(query.descriptionEn.getMetadata().getName()).isEqualTo("descriptionEn");
        assertThat(query.descriptionJa.getMetadata().getName()).isEqualTo("descriptionJa");
        assertThat(query.descriptionVi.getMetadata().getName()).isEqualTo("descriptionVi");
        assertThat(query.startDate.getMetadata().getName()).isEqualTo("startDate");
        assertThat(query.endDate.getMetadata().getName()).isEqualTo("endDate");
        assertThat(query.periodType.getMetadata().getName()).isEqualTo("periodType");
        assertThat(query.partnershipCollege).isNotNull();
        assertThat(query.partnershipDepartment).isNotNull();
        assertThat(query.partnershipRestaurant).isNotNull();
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
        assertThat(query.storeNameJa.getMetadata().getName()).isEqualTo("storeNameJa");
        assertThat(query.storeNameVi.getMetadata().getName()).isEqualTo("storeNameVi");
        assertThat(query.latitude.getMetadata().getName()).isEqualTo("latitude");
        assertThat(query.longitude.getMetadata().getName()).isEqualTo("longitude");
        assertThat(query.naverMapUrl.getMetadata().getName()).isEqualTo("naverMapUrl");
        assertThat(query.kakaoMapUrl.getMetadata().getName()).isEqualTo("kakaoMapUrl");
        assertThat(query.restaurantType.getMetadata().getName()).isEqualTo("restaurantType");
        assertThat(query.likes.getMetadata().getName()).isEqualTo("likes");
        assertThat(query.partnerships.getMetadata().getName()).isEqualTo("partnerships");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata partnershipMetadata = QPartnership.partnership.getMetadata();
        assertThat(new QPartnership("partnership").getType()).isEqualTo(Partnership.class);
        assertThat(new QPartnership(QPartnership.partnership).getType()).isEqualTo(Partnership.class);
        assertThat(new QPartnership(partnershipMetadata).getType()).isEqualTo(Partnership.class);
        assertThat(new QPartnership(partnershipMetadata, PathInits.DIRECT2).getType()).isEqualTo(Partnership.class);
        assertThat(new QPartnership(Partnership.class, partnershipMetadata, PathInits.DIRECT2).getType())
                .isEqualTo(Partnership.class);

        PathMetadata likeMetadata = QPartnershipLike.partnershipLike.getMetadata();
        assertThat(new QPartnershipLike("partnershipLike").getType()).isEqualTo(PartnershipLike.class);
        assertThat(new QPartnershipLike(QPartnershipLike.partnershipLike).getType()).isEqualTo(PartnershipLike.class);
        assertThat(new QPartnershipLike(likeMetadata).getType()).isEqualTo(PartnershipLike.class);
        assertThat(new QPartnershipLike(likeMetadata, PathInits.DIRECT2).getType()).isEqualTo(PartnershipLike.class);
        assertThat(new QPartnershipLike(PartnershipLike.class, likeMetadata, PathInits.DIRECT2).getType())
                .isEqualTo(PartnershipLike.class);

        PathMetadata restaurantMetadata = QPartnershipRestaurant.partnershipRestaurant.getMetadata();
        assertThat(new QPartnershipRestaurant("partnershipRestaurant").getType()).isEqualTo(PartnershipRestaurant.class);
        assertThat(new QPartnershipRestaurant(QPartnershipRestaurant.partnershipRestaurant).getType())
                .isEqualTo(PartnershipRestaurant.class);
        assertThat(new QPartnershipRestaurant(restaurantMetadata).getType()).isEqualTo(PartnershipRestaurant.class);
    }
}
