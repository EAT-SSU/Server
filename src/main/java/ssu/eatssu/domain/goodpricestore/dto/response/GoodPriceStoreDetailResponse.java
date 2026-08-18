package ssu.eatssu.domain.goodpricestore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.goodpricestore.entity.CategoryType;
import ssu.eatssu.domain.goodpricestore.entity.GoodPriceStore;

@Getter
@Builder
@AllArgsConstructor
public class GoodPriceStoreDetailResponse {
    private Long id;
    private String storeName;
    private CategoryType category;
    private String mainMenu;
    private Integer price;
    private String roadAddress;
    private String imageUrl;

    public static GoodPriceStoreDetailResponse fromEntity(GoodPriceStore store) {
        return GoodPriceStoreDetailResponse.builder()
                                           .id(store.getId())
                                           .storeName(store.getStoreName())
                                           .category(store.getCategory())
                                           .mainMenu(store.getMainMenu())
                                           .price(store.getPrice())
                                           .roadAddress(store.getRoadAddress())
                                           .imageUrl(store.getRepresentativeImageUrl())
                                           .build();
    }
}
