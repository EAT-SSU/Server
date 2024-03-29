package ssu.eatssu.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MenuCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_category_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Restaurant restaurant;

}
