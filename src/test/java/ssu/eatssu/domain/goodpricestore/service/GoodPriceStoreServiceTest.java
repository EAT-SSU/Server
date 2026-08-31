package ssu.eatssu.domain.goodpricestore.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.goodpricestore.entity.CategoryType;
import ssu.eatssu.domain.goodpricestore.entity.GoodPriceStore;
import ssu.eatssu.domain.goodpricestore.persistence.GoodPriceStoreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GoodPriceStoreServiceTest {

    @Test
    void 카테고리로_착한가격업소를_조회한다() {
        GoodPriceStoreRepository repository = mock(GoodPriceStoreRepository.class);
        GoodPriceStore store = GoodPriceStore.builder()
                                             .sourceId(1).category(CategoryType.KOREAN).storeName("식당")
                                             .roadAddress("서울").district("용산구").latitude(37.5).longitude(127.0).build();
        when(repository.findAllByCategory(CategoryType.KOREAN)).thenReturn(List.of(store));

        assertThat(new GoodPriceStoreService(repository).getStores(CategoryType.KOREAN))
                .extracting("storeName")
                .containsExactly("식당");
    }
}
