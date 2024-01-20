//package ssu.eatssu.domain.restaurant.entity;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.List;
//import ssu.eatssu.domain.menu.entity.Meal;
//import ssu.eatssu.domain.menu.entity.Menu;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class TemporalRestaurant {
//
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "restaurant_id")
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    private Restaurant restaurantName;
//
//    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
//    private List<Menu> menus;
//
//    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
//    private List<Meal> meals;
//}
