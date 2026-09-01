package ssu.eatssu.domain.goodpricestore.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GoodPriceStoreTest {

    @Test
    void representativeImagePrefersFirstAvailableImage() {
        GoodPriceStore first = store("one", "two", "three");
        GoodPriceStore second = store(null, "two", "three");
        GoodPriceStore third = store(null, null, "three");
        GoodPriceStore none = store(null, null, null);

        assertThat(first.getRepresentativeImageUrl()).isEqualTo("one");
        assertThat(second.getRepresentativeImageUrl()).isEqualTo("two");
        assertThat(third.getRepresentativeImageUrl()).isEqualTo("three");
        assertThat(none.getRepresentativeImageUrl()).isNull();

        assertThat(first.getSourceId()).isEqualTo(1);
        assertThat(first.getDistrict()).isEqualTo("동작구");
        assertThat(first.getImageUrl1()).isEqualTo("one");
        assertThat(first.getImageUrl2()).isEqualTo("two");
        assertThat(first.getImageUrl3()).isEqualTo("three");
    }

    private GoodPriceStore store(String image1, String image2, String image3) {
        return GoodPriceStore.builder().sourceId(1).category(CategoryType.ETC).storeName("가게")
                .roadAddress("서울").district("동작구").latitude(37.5).longitude(126.9)
                .imageUrl1(image1).imageUrl2(image2).imageUrl3(image3).build();
    }
}
