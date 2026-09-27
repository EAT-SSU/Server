package ssu.eatssu.domain.favorite.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.favorite.entity.MenuFavorite;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MenuFavoritePersistenceTest {

    @Autowired
    private MenuFavoriteRepository menuFavoriteRepository;

    @Autowired
    private MenuFavoriteQueryRepository menuFavoriteQueryRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        menuFavoriteRepository.deleteAll();
        menuRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void searchMatchesKoreanNameIgnoringCaseAndSkipsDiscontinuedMenus() {
        // given
        menuRepository.save(Menu.createVariable("Pasta 샐러드", Restaurant.HAKSIK));
        Menu discontinued = Menu.createVariable("단종파스타", Restaurant.DODAM);
        discontinued.changeDiscontinuedStatus();
        menuRepository.save(discontinued);

        // when
        List<Menu> byEnglishWord = menuFavoriteQueryRepository.searchMenus("pASTA", Language.KO);
        List<Menu> byDiscontinuedName = menuFavoriteQueryRepository.searchMenus("파스타", Language.KO);

        // then
        assertThat(byEnglishWord).extracting(Menu::getName).containsExactly("Pasta 샐러드");
        assertThat(byDiscontinuedName).isEmpty();
    }

    @Test
    void searchMatchesTranslatedNameOnlyForThatLanguage() {
        // given
        Menu cutlet = Menu.createVariable("돈까스", Restaurant.HAKSIK);
        ReflectionTestUtils.setField(cutlet, "nameEn", "Pork Cutlet");
        ReflectionTestUtils.setField(cutlet, "nameJa", "トンカツ");
        ReflectionTestUtils.setField(cutlet, "nameVi", "Thit heo");
        menuRepository.save(cutlet);

        // when & then
        assertThat(menuFavoriteQueryRepository.searchMenus("pork", Language.EN)).hasSize(1);
        assertThat(menuFavoriteQueryRepository.searchMenus("トンカ", Language.JA)).hasSize(1);
        assertThat(menuFavoriteQueryRepository.searchMenus("heo", Language.VI)).hasSize(1);
        assertThat(menuFavoriteQueryRepository.searchMenus("pork", Language.KO)).isEmpty();
        assertThat(menuFavoriteQueryRepository.searchMenus("pork", Language.JA)).isEmpty();
    }

    @Test
    void searchTreatsLikeWildcardsAsPlainCharacters() {
        // given
        menuRepository.save(Menu.createVariable("100%주스", Restaurant.SNACK_CORNER));
        menuRepository.save(Menu.createVariable("오렌지주스", Restaurant.SNACK_CORNER));

        // when
        List<Menu> menus = menuFavoriteQueryRepository.searchMenus("0%", Language.KO);

        // then
        assertThat(menus).extracting(Menu::getName).containsExactly("100%주스");
    }

    @Test
    void favoriteQueriesReturnLatestFirstAndDeleteOnlyTheGivenMenu() {
        // given
        User user = userRepository.save(user("favorite@eatssu.com"));
        Menu older = menuRepository.save(Menu.createVariable("김치찌개", Restaurant.HAKSIK));
        Menu newer = menuRepository.save(Menu.createVariable("파스타", Restaurant.DODAM));
        Menu notFavorite = menuRepository.save(Menu.createVariable("라면", Restaurant.DORMITORY));
        menuFavoriteRepository.save(MenuFavorite.of(user, older));
        menuFavoriteRepository.save(MenuFavorite.of(user, newer));

        // when
        List<MenuFavorite> favorites = transactionTemplate.execute(
                status -> menuFavoriteRepository.findAllWithMenuByUserId(user.getId()));
        Integer deleted = transactionTemplate.execute(
                status -> menuFavoriteRepository.deleteByUserIdAndMenuId(user.getId(), older.getId()));

        // then
        assertThat(favorites).extracting(favorite -> favorite.getMenu().getName()).containsExactly("파스타", "김치찌개");
        assertThat(menuFavoriteRepository.findFavoriteMenuIds(user.getId(),
                                                              List.of(older.getId(), newer.getId(), notFavorite.getId())))
                .containsExactly(newer.getId());
        assertThat(deleted).isEqualTo(1);
        assertThat(menuFavoriteRepository.existsByUserIdAndMenuId(user.getId(), older.getId())).isFalse();
        assertThat(menuFavoriteRepository.existsByUserIdAndMenuId(user.getId(), newer.getId())).isTrue();
    }

    @Test
    void withdrawingUserOrDeletingMenuRemovesFavorites() {
        // given
        User withdrawn = userRepository.save(user("withdrawn@eatssu.com"));
        User remaining = userRepository.save(user("remaining@eatssu.com"));
        Menu kept = menuRepository.save(Menu.createVariable("김치찌개", Restaurant.HAKSIK));
        Menu removed = menuRepository.save(Menu.createVariable("파스타", Restaurant.DODAM));
        menuFavoriteRepository.save(MenuFavorite.of(withdrawn, kept));
        menuFavoriteRepository.save(MenuFavorite.of(remaining, kept));
        menuFavoriteRepository.save(MenuFavorite.of(remaining, removed));

        // when
        userRepository.deleteById(withdrawn.getId());
        menuRepository.deleteById(removed.getId());

        // then
        assertThat(menuFavoriteRepository.count()).isEqualTo(1);
        assertThat(menuFavoriteRepository.existsByUserIdAndMenuId(remaining.getId(), kept.getId())).isTrue();
    }

    private User user(String email) {
        return User.create(email, "tester", OAuthProvider.KAKAO, email, "credentials");
    }
}
