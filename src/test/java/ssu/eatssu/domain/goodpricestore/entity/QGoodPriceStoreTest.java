package ssu.eatssu.domain.goodpricestore.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QGoodPriceStoreTest {

    @Test
    void exposesQuerydslPathsForStoreFields() {
        QGoodPriceStore query = QGoodPriceStore.goodPriceStore;

        assertThat(query.getType()).isEqualTo(GoodPriceStore.class);
        assertThat(query.storeName.getMetadata().getName()).isEqualTo("storeName");
        assertThat(query.category.getMetadata().getName()).isEqualTo("category");
        assertThat(query.latitude.getMetadata().getName()).isEqualTo("latitude");
        assertThat(query.sourceId.getMetadata().getName()).isEqualTo("sourceId");
    }
}
