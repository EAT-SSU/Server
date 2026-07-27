package ssu.eatssu.domain.partnership.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.partnership.entity.RestaurantType;
import ssu.eatssu.domain.user.entity.Language;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class PartnershipResponse {
    private String storeName;
    private Double longitude;
    private Double latitude;
    private RestaurantType restaurantType;
    private String naverMapUrl;
    private String kakaoMapUrl;
    private List<PartnershipInfo> partnershipInfos;

    public static PartnershipResponse fromEntity(PartnershipRestaurant restaurant, Long userId, Language language) {
        boolean isLiked = restaurant.getLikes().stream()
                                    .anyMatch(like -> like.getUser().getId().equals(userId));

        List<PartnershipInfo> infos = restaurant.getPartnerships().stream()
                                                .map(partnership -> PartnershipInfo.fromEntity(partnership,
                                                                                               restaurant,
                                                                                               isLiked,
                                                                                               language))
                                                .collect(Collectors.toList());

        return PartnershipResponse.builder()
                                  .storeName(restaurant.getStoreNameByLanguage(language))
                                  .longitude(restaurant.getLongitude())
                                  .latitude(restaurant.getLatitude())
                                  .restaurantType(restaurant.getRestaurantType())
                                  .naverMapUrl(restaurant.getNaverMapUrl())
                                  .kakaoMapUrl(restaurant.getKakaoMapUrl())
                                  .partnershipInfos(infos)
                                  .build();
    }
}
