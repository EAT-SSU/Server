package ssu.eatssu.domain.goodpricestore.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.goodpricestore.entity.CategoryType;
import ssu.eatssu.domain.goodpricestore.entity.GoodPriceStore;

import java.util.List;

public interface GoodPriceStoreRepository extends JpaRepository<GoodPriceStore, Long> {
    List<GoodPriceStore> findAllByCategory(CategoryType category);
}
