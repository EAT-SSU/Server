package ssu.eatssu.domain.partnership.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartnershipRestaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partnershipRestaurant_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "restaurant_type", nullable = false)
    private RestaurantType restaurantType;

    @Column(name = "longitude", nullable = false)
    private Double longitude; // 경도 == x축

    @Column(name = "latitude", nullable = false)
    private Double latitude; // 위도 == y축
    @Column(name = "store_name", nullable = false)
    private String storeName;

    @OneToMany(mappedBy = "partnershipRestaurant")
    @BatchSize(size = 20)
    private List<PartnershipLike> likes = new ArrayList<>();
    @OneToMany(mappedBy = "partnershipRestaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partnership> partnerships = new ArrayList<>();
}
