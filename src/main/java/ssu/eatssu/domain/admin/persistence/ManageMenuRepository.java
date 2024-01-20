package ssu.eatssu.domain.admin.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.menu.entity.Menu;

@Repository
public class ManageMenuRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JpaEntityInformation<Menu, ?> entityInformation;

    public ManageMenuRepository(EntityManager em) {
        this.em = em;
        this.entityInformation =
                JpaEntityInformationSupport.getEntityInformation(Menu.class, em);
    }

    @Transactional
    public Menu save(Menu menu) {
        if (entityInformation.isNew(menu)) {
            em.persist(menu);
            return menu;
        } else {
            return em.merge(menu);
        }
    }

    @Transactional
    public void delete(Menu menu) {

        if (entityInformation.isNew(menu)) {
            return;
        }

        Menu existing = em.find(Menu.class, menu.getId());

        if (existing == null) {
            return;
        }

        em.remove(em.contains(menu) ? menu : em.merge(menu));
    }

}
