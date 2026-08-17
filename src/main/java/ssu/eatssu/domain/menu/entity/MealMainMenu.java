package ssu.eatssu.domain.menu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.user.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealMainMenu extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_main_menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    private int seq;

    @Column(name = "name_ko")
    private String nameKo;

    @Column(name = "name_en")
    private String nameEn;

    private MealMainMenu(Meal meal, int seq, String nameKo, String nameEn) {
        this.meal = meal;
        this.seq = seq;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
    }

    public static MealMainMenu of(Meal meal, int seq, String nameKo, String nameEn) {
        return new MealMainMenu(meal, seq, nameKo, nameEn);
    }
}
