package ssu.eatssu.domain.goodpricestore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.goodpricestore.entity.CategoryType;
import ssu.eatssu.domain.goodpricestore.entity.GoodPriceStore;

@Getter
@Builder
@AllArgsConstructor
public class GoodPriceStoreResponse {
    private Long id;
    private String storeName;
    private CategoryType category;
    private Double latitude;
    private Double longitude;

    public static GoodPriceStoreResponse fromEntity(GoodPriceStore store) {
        return GoodPriceStoreResponse.builder()
                                     .id(store.getId())
                                     .storeName(store.getStoreName())
                                     .category(store.getCategory())
                                     .latitude(store.getLatitude())
                                     .longitude(store.getLongitude())
                                     .build();
    }
}
