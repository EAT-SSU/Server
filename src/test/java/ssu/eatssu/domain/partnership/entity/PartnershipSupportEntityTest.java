package ssu.eatssu.domain.partnership.entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PartnershipSupportEntityTest {

    @Test
    void partnershipLikeLinksUserAndRestaurant() {
        User user = mock(User.class);
        PartnershipRestaurant restaurant = new PartnershipRestaurant();

        PartnershipLike like = new PartnershipLike(user, restaurant);

        assertThat(like.getUser()).isSameAs(user);
        assertThat(like.getPartnershipRestaurant()).isSameAs(restaurant);
        assertThat(like.getId()).isNull();
    }

    @Test
    void partnershipRestaurantExposesAllFields() {
        PartnershipRestaurant restaurant = new PartnershipRestaurant();
        ReflectionTestUtils.setField(restaurant, "id", 1L);
        ReflectionTestUtils.setField(restaurant, "restaurantType", RestaurantType.RESTAURANT);
        ReflectionTestUtils.setField(restaurant, "longitude", 126.9576);
        ReflectionTestUtils.setField(restaurant, "latitude", 37.4963);
        ReflectionTestUtils.setField(restaurant, "storeNameEn", "Test Store");
        ReflectionTestUtils.setField(restaurant, "storeNameJa", "テストストア");
        ReflectionTestUtils.setField(restaurant, "storeNameVi", "Cửa hàng thử nghiệm");
        ReflectionTestUtils.setField(restaurant, "naverMapUrl", "https://map.naver.com/p/example");
        ReflectionTestUtils.setField(restaurant, "kakaoMapUrl", "https://map.kakao.com/link/map/example");

        assertThat(restaurant.getId()).isEqualTo(1L);
        assertThat(restaurant.getRestaurantType()).isEqualTo(RestaurantType.RESTAURANT);
        assertThat(restaurant.getLongitude()).isEqualTo(126.9576);
        assertThat(restaurant.getLatitude()).isEqualTo(37.4963);
        assertThat(restaurant.getStoreNameEn()).isEqualTo("Test Store");
        assertThat(restaurant.getStoreNameJa()).isEqualTo("テストストア");
        assertThat(restaurant.getStoreNameVi()).isEqualTo("Cửa hàng thử nghiệm");
        assertThat(restaurant.getNaverMapUrl()).isEqualTo("https://map.naver.com/p/example");
        assertThat(restaurant.getKakaoMapUrl()).isEqualTo("https://map.kakao.com/link/map/example");
        assertThat(restaurant.getLikes()).isEmpty();
        assertThat(restaurant.getPartnerships()).isEmpty();
    }
}
