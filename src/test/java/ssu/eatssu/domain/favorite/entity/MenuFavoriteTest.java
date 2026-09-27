package ssu.eatssu.domain.favorite.entity;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MenuFavoriteTest {

    @Test
    void ofCreatesFavoriteLinkingUserAndMenu() {
        // given
        User user = mock(User.class);
        Menu menu = Menu.createVariable("돈까스", Restaurant.HAKSIK);

        // when
        MenuFavorite favorite = MenuFavorite.of(user, menu);

        // then
        assertThat(favorite.getId()).isNull();
        assertThat(favorite.getUser()).isSameAs(user);
        assertThat(favorite.getMenu()).isSameAs(menu);
    }
}
