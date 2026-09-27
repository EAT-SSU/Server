package ssu.eatssu.domain.favorite.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssu.eatssu.domain.favorite.entity.MenuFavorite;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface MenuFavoriteRepository extends JpaRepository<MenuFavorite, Long> {

    boolean existsByUserIdAndMenuId(Long userId, Long menuId);

    @Query("SELECT f FROM MenuFavorite f JOIN FETCH f.menu WHERE f.user.id = :userId "
            + "ORDER BY f.createdDate DESC, f.id DESC")
    List<MenuFavorite> findAllWithMenuByUserId(@Param("userId") Long userId);

    @Query("SELECT f.menu.id FROM MenuFavorite f WHERE f.user.id = :userId AND f.menu.id IN :menuIds")
    Set<Long> findFavoriteMenuIds(@Param("userId") Long userId, @Param("menuIds") Collection<Long> menuIds);

    @Modifying
    @Query("DELETE FROM MenuFavorite f WHERE f.user.id = :userId AND f.menu.id = :menuId")
    int deleteByUserIdAndMenuId(@Param("userId") Long userId, @Param("menuId") Long menuId);
}
