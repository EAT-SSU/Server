package ssu.eatssu.domain.admin.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ssu.eatssu.domain.menu.entity.QMenuCategory;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

@Repository
@RequiredArgsConstructor
public class LoadCategoryRepository {

    private final JPAQueryFactory queryFactory;
    private final QMenuCategory category = QMenuCategory.menuCategory;

    public boolean existsCategory(Restaurant restaurant, String name) {
        return queryFactory.select(category.id)
                           .from(category)
                           .where(category.name.eq(name),
                                  category.restaurant.eq(restaurant))
                           .fetchFirst() != null;
    }
}
