package ssu.eatssu.domain.notification.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.favorite.entity.MenuFavorite;
import ssu.eatssu.domain.favorite.persistence.MenuFavoriteRepository;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.notification.entity.MenuFavoritePushHistory;
import ssu.eatssu.domain.notification.entity.PushToken;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MenuFavoritePushPersistenceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 9, 28);

    @Autowired
    private MenuFavoriteMatchQueryRepository menuFavoriteMatchQueryRepository;

    @Autowired
    private PushTokenRepository pushTokenRepository;

    @Autowired
    private MenuFavoritePushHistoryRepository menuFavoritePushHistoryRepository;

    @Autowired
    private MenuFavoriteRepository menuFavoriteRepository;

    @Autowired
    private MealMenuRepository mealMenuRepository;

    @Autowired
    private MealRepository mealRepository;

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
        menuFavoritePushHistoryRepository.deleteAll();
        pushTokenRepository.deleteAll();
        menuFavoriteRepository.deleteAll();
        mealMenuRepository.deleteAll();
        mealRepository.deleteAll();
        menuRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findMatchesReturnsTodaysLunchAndDinnerMenusServedAtTheFavoritedRestaurant() {
        // given
        User fan = userRepository.save(user("fan@eatssu.com"));
        User other = userRepository.save(user("other@eatssu.com"));
        Menu favorite = menuRepository.save(Menu.createVariable("돈까스", Restaurant.HAKSIK));
        Menu duplicatedRow = menuRepository.save(Menu.createVariable("돈까스", Restaurant.HAKSIK));
        Menu otherRestaurant = menuRepository.save(Menu.createVariable("돈까스", Restaurant.DODAM));
        Menu notServed = menuRepository.save(Menu.createVariable("김치찌개", Restaurant.HAKSIK));
        menuFavoriteRepository.save(MenuFavorite.of(fan, favorite));
        menuFavoriteRepository.save(MenuFavorite.of(other, notServed));

        serve(TODAY, TimePart.LUNCH, Restaurant.HAKSIK, favorite, duplicatedRow);
        serve(TODAY, TimePart.DINNER, Restaurant.HAKSIK, duplicatedRow);
        serve(TODAY, TimePart.LUNCH, Restaurant.DODAM, otherRestaurant);
        serve(TODAY, TimePart.MORNING, Restaurant.HAKSIK, favorite);
        serve(TODAY.minusDays(1), TimePart.LUNCH, Restaurant.HAKSIK, favorite);

        // when
        List<MenuFavoriteMatch> matches = menuFavoriteMatchQueryRepository.findMatches(
                TODAY, List.of(TimePart.LUNCH, TimePart.DINNER));

        // then
        assertThat(matches).containsExactlyInAnyOrder(
                new MenuFavoriteMatch(fan.getId(), "돈까스", Restaurant.HAKSIK, TimePart.LUNCH),
                new MenuFavoriteMatch(fan.getId(), "돈까스", Restaurant.HAKSIK, TimePart.DINNER));
    }

    @Test
    void pushTokenQueriesFindAndDeleteTokens() {
        // given
        User owner = userRepository.save(user("owner@eatssu.com"));
        User stranger = userRepository.save(user("stranger@eatssu.com"));
        pushTokenRepository.save(PushToken.of(owner, "phone", DeviceType.IOS));
        pushTokenRepository.save(PushToken.of(owner, "tablet", DeviceType.ANDROID));
        pushTokenRepository.save(PushToken.of(stranger, "stranger-phone", DeviceType.ANDROID));

        // when
        List<UserPushToken> ownerTokens = pushTokenRepository.findAllByUserIds(List.of(owner.getId()));
        Integer deletedByStranger = transactionTemplate.execute(
                status -> pushTokenRepository.deleteByUserIdAndToken(stranger.getId(), "phone"));
        int deletedInvalid = pushTokenRepository.deleteAllByTokens(List.of("phone", "stranger-phone"));

        // then
        assertThat(ownerTokens).containsExactlyInAnyOrder(new UserPushToken(owner.getId(), "phone"),
                                                          new UserPushToken(owner.getId(), "tablet"));
        assertThat(deletedByStranger).isZero();
        assertThat(deletedInvalid).isEqualTo(2);
        assertThat(pushTokenRepository.findByToken("tablet")).isPresent();
        assertThat(pushTokenRepository.findByToken("phone")).isEmpty();
    }

    @Test
    void pushHistoryAllowsOnlyOnePushPerUserPerDay() {
        // given
        User user = userRepository.save(user("history@eatssu.com"));
        menuFavoritePushHistoryRepository.save(MenuFavoritePushHistory.of(user, TODAY));

        // when & then
        assertThat(menuFavoritePushHistoryRepository.findUserIdsByPushDate(TODAY)).containsExactly(user.getId());
        assertThat(menuFavoritePushHistoryRepository.findUserIdsByPushDate(TODAY.plusDays(1))).isEmpty();
        assertThatThrownBy(() -> menuFavoritePushHistoryRepository.save(MenuFavoritePushHistory.of(user, TODAY)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void withdrawingUserRemovesTokensAndPushHistory() {
        // given
        User user = userRepository.save(user("withdraw@eatssu.com"));
        pushTokenRepository.save(PushToken.of(user, "phone", DeviceType.IOS));
        menuFavoritePushHistoryRepository.save(MenuFavoritePushHistory.of(user, TODAY));

        // when
        userRepository.deleteById(user.getId());

        // then
        assertThat(pushTokenRepository.count()).isZero();
        assertThat(menuFavoritePushHistoryRepository.count()).isZero();
    }

    private void serve(LocalDate date, TimePart timePart, Restaurant restaurant, Menu... menus) {
        Meal meal = mealRepository.save(new Meal(Date.valueOf(date), timePart, restaurant));
        for (Menu menu : menus) {
            mealMenuRepository.save(MealMenu.builder().meal(meal).menu(menu).build());
        }
    }

    private User user(String email) {
        return User.create(email, "tester", OAuthProvider.KAKAO, email, "credentials");
    }
}
