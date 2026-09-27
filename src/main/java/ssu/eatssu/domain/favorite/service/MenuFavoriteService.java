package ssu.eatssu.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.favorite.dto.response.FavoriteMenuResponse;
import ssu.eatssu.domain.favorite.dto.response.MenuSearchResponse;
import ssu.eatssu.domain.favorite.entity.MenuFavorite;
import ssu.eatssu.domain.favorite.persistence.MenuFavoriteQueryRepository;
import ssu.eatssu.domain.favorite.persistence.MenuFavoriteRepository;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.DISCONTINUED_MENU;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.INVALID_SEARCH_KEYWORD;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_MENU;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuFavoriteService {

    private static final int MIN_KEYWORD_LENGTH = 2;
    private static final int EXACT_MATCH = 0;
    private static final int PREFIX_MATCH = 1;
    private static final int PARTIAL_MATCH = 2;

    private final MenuFavoriteRepository menuFavoriteRepository;
    private final MenuFavoriteQueryRepository menuFavoriteQueryRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    public List<MenuSearchResponse> searchMenus(CustomUserDetails userDetails, String keyword) {
        String normalizedKeyword = normalizeKeyword(keyword);
        User user = findUser(userDetails);
        Language language = user.getLanguage();

        List<Menu> menus = sortAndDeduplicate(menuFavoriteQueryRepository.searchMenus(normalizedKeyword, language),
                                              normalizedKeyword, language);
        if (menus.isEmpty()) {
            return List.of();
        }

        Set<Long> favoriteMenuIds = menuFavoriteRepository.findFavoriteMenuIds(user.getId(),
                                                                               menus.stream().map(Menu::getId).toList());
        return menus.stream()
                    .map(menu -> MenuSearchResponse.of(menu, language, favoriteMenuIds.contains(menu.getId())))
                    .toList();
    }

    public List<FavoriteMenuResponse> getFavoriteMenus(CustomUserDetails userDetails) {
        User user = findUser(userDetails);
        return menuFavoriteRepository.findAllWithMenuByUserId(user.getId()).stream()
                                     .map(favorite -> FavoriteMenuResponse.of(favorite.getMenu(), user.getLanguage()))
                                     .toList();
    }

    @Transactional
    public void addFavorite(CustomUserDetails userDetails, Long menuId) {
        User user = findUser(userDetails);
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        if (menuFavoriteRepository.existsByUserIdAndMenuId(user.getId(), menu.getId())) {
            return;
        }
        if (menu.isDiscontinued()) {
            throw new BaseException(DISCONTINUED_MENU);
        }
        menuFavoriteRepository.save(MenuFavorite.of(user, menu));
    }

    @Transactional
    public void removeFavorite(CustomUserDetails userDetails, Long menuId) {
        menuFavoriteRepository.deleteByUserIdAndMenuId(userDetails.getId(), menuId);
    }

    private String normalizeKeyword(String keyword) {
        String stripped = keyword == null ? "" : keyword.strip();
        if (stripped.codePointCount(0, stripped.length()) < MIN_KEYWORD_LENGTH) {
            throw new BaseException(INVALID_SEARCH_KEYWORD);
        }
        return stripped;
    }

    private List<Menu> sortAndDeduplicate(List<Menu> menus, String keyword, Language language) {
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        Map<String, Menu> uniqueMenus = new LinkedHashMap<>();

        menus.stream()
             .sorted(Comparator.comparingInt((Menu menu) -> matchRank(menu, lowerKeyword, language))
                               .thenComparing(menu -> menu.getNameByLanguage(language))
                               .thenComparing(Menu::getId))
             .forEach(menu -> uniqueMenus.putIfAbsent(menu.getName() + "|" + menu.getRestaurant(), menu));

        return List.copyOf(uniqueMenus.values());
    }

    private int matchRank(Menu menu, String lowerKeyword, Language language) {
        return Stream.of(menu.getName(), menu.getNameByLanguage(language))
                     .mapToInt(name -> rankOf(name.toLowerCase(Locale.ROOT), lowerKeyword))
                     .min()
                     .orElse(PARTIAL_MATCH);
    }

    private int rankOf(String name, String lowerKeyword) {
        if (name.equals(lowerKeyword)) {
            return EXACT_MATCH;
        }
        if (name.startsWith(lowerKeyword)) {
            return PREFIX_MATCH;
        }
        return PARTIAL_MATCH;
    }

    private User findUser(CustomUserDetails userDetails) {
        return userRepository.findById(userDetails.getId()).orElseThrow(() -> new BaseException(NOT_FOUND_USER));
    }
}
