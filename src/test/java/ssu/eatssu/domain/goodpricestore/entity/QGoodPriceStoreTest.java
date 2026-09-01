package ssu.eatssu.domain.goodpricestore.entity;

import com.querydsl.core.types.PathMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QGoodPriceStoreTest {

    @Test
    void exposesQuerydslPathsForStoreFields() {
        QGoodPriceStore query = QGoodPriceStore.goodPriceStore;

        assertThat(query.getType()).isEqualTo(GoodPriceStore.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.storeName.getMetadata().getName()).isEqualTo("storeName");
        assertThat(query.category.getMetadata().getName()).isEqualTo("category");
        assertThat(query.mainMenu.getMetadata().getName()).isEqualTo("mainMenu");
        assertThat(query.price.getMetadata().getName()).isEqualTo("price");
        assertThat(query.roadAddress.getMetadata().getName()).isEqualTo("roadAddress");
        assertThat(query.district.getMetadata().getName()).isEqualTo("district");
        assertThat(query.latitude.getMetadata().getName()).isEqualTo("latitude");
        assertThat(query.longitude.getMetadata().getName()).isEqualTo("longitude");
        assertThat(query.imageUrl1.getMetadata().getName()).isEqualTo("imageUrl1");
        assertThat(query.imageUrl2.getMetadata().getName()).isEqualTo("imageUrl2");
        assertThat(query.imageUrl3.getMetadata().getName()).isEqualTo("imageUrl3");
        assertThat(query.naverMapUrl.getMetadata().getName()).isEqualTo("naverMapUrl");
        assertThat(query.kakaoMapUrl.getMetadata().getName()).isEqualTo("kakaoMapUrl");
        assertThat(query.sourceId.getMetadata().getName()).isEqualTo("sourceId");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QGoodPriceStore.goodPriceStore.getMetadata();

        assertThat(new QGoodPriceStore("goodPriceStore").getType()).isEqualTo(GoodPriceStore.class);
        assertThat(new QGoodPriceStore(QGoodPriceStore.goodPriceStore).getType()).isEqualTo(GoodPriceStore.class);
        assertThat(new QGoodPriceStore(metadata).getType()).isEqualTo(GoodPriceStore.class);
    }
}
