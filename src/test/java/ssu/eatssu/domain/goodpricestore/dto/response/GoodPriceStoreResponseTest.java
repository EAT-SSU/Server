package ssu.eatssu.domain.goodpricestore.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.goodpricestore.entity.CategoryType;
import ssu.eatssu.domain.goodpricestore.entity.GoodPriceStore;

import static org.assertj.core.api.Assertions.assertThat;

class GoodPriceStoreResponseTest {

    @Test
    void listAndDetailResponsesMapStoreFields() {
        GoodPriceStore store = GoodPriceStore.builder().sourceId(1).category(CategoryType.KOREAN)
                .storeName("식당").mainMenu("비빔밥").price(6000).roadAddress("서울")
                .district("동작구").latitude(37.5).longitude(126.9).imageUrl2("image2")
                .naverMapUrl("naver").kakaoMapUrl("kakao").build();
        ReflectionTestUtils.setField(store, "id", 1L);

        GoodPriceStoreResponse list = GoodPriceStoreResponse.fromEntity(store);
        GoodPriceStoreDetailResponse detail = GoodPriceStoreDetailResponse.fromEntity(store);

        assertThat(list.getStoreName()).isEqualTo("식당");
        assertThat(list.getLatitude()).isEqualTo(37.5);
        assertThat(detail.getMainMenu()).isEqualTo("비빔밥");
        assertThat(detail.getImageUrl()).isEqualTo("image2");
        assertThat(detail.getNaverMapUrl()).isEqualTo("naver");
    }
}
