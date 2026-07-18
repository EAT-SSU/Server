package ssu.eatssu.domain.partnership.dto;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.partnership.entity.RestaurantType;
import ssu.eatssu.domain.user.entity.Language;

import static org.assertj.core.api.Assertions.assertThat;

class PartnershipResponseTest {

    @Test
    void shouldMapMapUrlsFromRestaurant() {
        // given
        PartnershipRestaurant restaurant = createRestaurant("https://map.naver.com/p/example",
                                                            "https://map.kakao.com/link/map/example");

        // when
        PartnershipResponse response = PartnershipResponse.fromEntity(restaurant, 1L, Language.KO);

        // then
        assertThat(response.getNaverMapUrl()).isEqualTo("https://map.naver.com/p/example");
        assertThat(response.getKakaoMapUrl()).isEqualTo("https://map.kakao.com/link/map/example");
    }

    @Test
    void shouldReturnNullWhenMapUrlsAreNotRegistered() {
        // given
        PartnershipRestaurant restaurant = createRestaurant(null, null);

        // when
        PartnershipResponse response = PartnershipResponse.fromEntity(restaurant, 1L, Language.KO);

        // then
        assertThat(response.getNaverMapUrl()).isNull();
        assertThat(response.getKakaoMapUrl()).isNull();
    }

    private PartnershipRestaurant createRestaurant(String naverMapUrl, String kakaoMapUrl) {
        PartnershipRestaurant restaurant = new TestPartnershipRestaurant();
        ReflectionTestUtils.setField(restaurant, "restaurantType", RestaurantType.RESTAURANT);
        ReflectionTestUtils.setField(restaurant, "longitude", 126.9576);
        ReflectionTestUtils.setField(restaurant, "latitude", 37.4963);
        ReflectionTestUtils.setField(restaurant, "storeNameKo", "테스트 가게");
        ReflectionTestUtils.setField(restaurant, "naverMapUrl", naverMapUrl);
        ReflectionTestUtils.setField(restaurant, "kakaoMapUrl", kakaoMapUrl);
        return restaurant;
    }

    private static class TestPartnershipRestaurant extends PartnershipRestaurant {
    }
}
