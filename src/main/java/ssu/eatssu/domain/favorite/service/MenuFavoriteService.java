package ssu.eatssu.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ssu.eatssu.domain.menu.util.MenuNameSanitizer;
import ssu.eatssu.domain.menu.util.SanitizedMenuName;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.DISCONTINUED_MENU;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.INVALID_SEARCH_KEYWORD;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_MENU;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

@Slf4j
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

        List<Menu> searchedMenus = menuFavoriteQueryRepository.searchMenus(normalizedKeyword, language);
        List<SearchCandidate> candidates = sortAndDeduplicate(toCandidates(searchedMenus, language), normalizedKeyword);
        if (candidates.isEmpty()) {
            return List.of();
        }

        Set<MenuGroup> favoriteGroups = favoriteGroupsOf(user.getId());
        return candidates.stream()
                         .map(candidate -> MenuSearchResponse.of(candidate.menu(), candidate.displayName(),
                                                                 favoriteGroups.contains(candidate.group())))
                         .toList();
    }

    public List<FavoriteMenuResponse> getFavoriteMenus(CustomUserDetails userDetails) {
        User user = findUser(userDetails);
        Map<MenuGroup, Menu> uniqueMenus = new LinkedHashMap<>();
        menuFavoriteRepository.findAllWithMenuByUserId(user.getId())
                              .forEach(favorite -> uniqueMenus.putIfAbsent(MenuGroup.of(favorite.getMenu()),
                                                                           favorite.getMenu()));
        return uniqueMenus.values().stream()
                          .map(menu -> FavoriteMenuResponse.of(menu, user.getLanguage()))
                          .toList();
    }

    @Transactional
    public void addFavorite(CustomUserDetails userDetails, Long menuId) {
        User user = findUser(userDetails);
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        if (favoriteGroupsOf(user.getId()).contains(MenuGroup.of(menu))) {
            return;
        }
        if (menu.isDiscontinued()) {
            throw new BaseException(DISCONTINUED_MENU);
        }
        menuFavoriteRepository.save(MenuFavorite.of(user, menu));
    }

    @Transactional
    public void removeFavorite(CustomUserDetails userDetails, Long menuId) {
        Long userId = userDetails.getId();
        Menu menu = menuRepository.findById(menuId).orElse(null);
        if (menu == null) {
            menuFavoriteRepository.deleteByUserIdAndMenuId(userId, menuId);
            return;
        }
        MenuGroup group = MenuGroup.of(menu);
        List<MenuFavorite> sameGroupFavorites = menuFavoriteRepository.findAllWithMenuByUserId(userId).stream()
                                                                      .filter(favorite -> MenuGroup.of(
                                                                              favorite.getMenu()).equals(group))
                                                                      .toList();
        menuFavoriteRepository.deleteAll(sameGroupFavorites);
    }

    private Set<MenuGroup> favoriteGroupsOf(Long userId) {
        return menuFavoriteRepository.findAllWithMenuByUserId(userId).stream()
                                     .map(favorite -> MenuGroup.of(favorite.getMenu()))
                                     .collect(Collectors.toSet());
    }

    private String normalizeKeyword(String keyword) {
        String stripped = keyword == null ? "" : keyword.strip();
        if (stripped.codePointCount(0, stripped.length()) < MIN_KEYWORD_LENGTH) {
            throw new BaseException(INVALID_SEARCH_KEYWORD);
        }
        return stripped;
    }

    private List<SearchCandidate> toCandidates(List<Menu> menus, Language language) {
        List<SearchCandidate> candidates = new ArrayList<>();
        for (Menu menu : menus) {
            SanitizedMenuName sanitized = MenuNameSanitizer.sanitize(menu.getName());
            if (sanitized.isExcluded()) {
                log.debug("찜 검색 결과에서 메뉴가 아닌 항목 제외. menuId={}, name=[{}], reason={}",
                          menu.getId(), menu.getName(), sanitized.excludeReason());
                continue;
            }
            candidates.add(new SearchCandidate(menu, MenuGroup.of(menu), sanitized.name(),
                                               displayName(menu, language, sanitized.name())));
        }
        return candidates;
    }

    private String displayName(Menu menu, Language language, String sanitizedName) {
        String localizedName = MenuNameSanitizer.normalize(menu.getNameByLanguage(language));
        return localizedName.isEmpty() ? sanitizedName : localizedName;
    }

    private List<SearchCandidate> sortAndDeduplicate(List<SearchCandidate> candidates, String keyword) {
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        Map<MenuGroup, SearchCandidate> uniqueCandidates = new LinkedHashMap<>();

        candidates.stream()
                  .sorted(Comparator.comparingInt((SearchCandidate candidate) -> matchRank(candidate, lowerKeyword))
                                    .thenComparing(SearchCandidate::displayName)
                                    .thenComparing(candidate -> candidate.menu().getId()))
                  .forEach(candidate -> uniqueCandidates.putIfAbsent(candidate.group(), candidate));

        return List.copyOf(uniqueCandidates.values());
    }

    private int matchRank(SearchCandidate candidate, String lowerKeyword) {
        return Stream.of(candidate.name(), candidate.displayName())
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

    private record SearchCandidate(Menu menu, MenuGroup group, String name, String displayName) {
    }

    private record MenuGroup(String nameKey, Restaurant restaurant) {

        private static MenuGroup of(Menu menu) {
            return new MenuGroup(MenuNameSanitizer.toKey(menu.getName()), menu.getRestaurant());
        }
    }
}
