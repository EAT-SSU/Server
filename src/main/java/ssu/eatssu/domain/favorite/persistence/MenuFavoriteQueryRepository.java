package ssu.eatssu.domain.favorite.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.QMenu;
import ssu.eatssu.domain.user.entity.Language;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuFavoriteQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QMenu menu = QMenu.menu;

    public List<Menu> searchMenus(String keyword, Language language) {
        return queryFactory.selectFrom(menu)
                           .where(menu.isDiscontinued.isFalse(), nameContains(keyword, language))
                           .fetch();
    }

    private BooleanExpression nameContains(String keyword, Language language) {
        BooleanExpression koreanNameContains = menu.name.containsIgnoreCase(keyword);
        StringPath localizedName = localizedNamePath(language);
        if (localizedName == null) {
            return koreanNameContains;
        }
        return koreanNameContains.or(localizedName.containsIgnoreCase(keyword));
    }

    private StringPath localizedNamePath(Language language) {
        return switch (language) {
            case EN -> menu.nameEn;
            case JA -> menu.nameJa;
            case VI -> menu.nameVi;
            case KO -> null;
        };
    }
}
