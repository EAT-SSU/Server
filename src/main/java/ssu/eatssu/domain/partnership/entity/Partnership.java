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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Partnership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partnership_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "partnership_type", nullable = false)
    private PartnershipType partnershipType;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "restaurant_type", nullable = false)
    private RestaurantType restaurantType;

    @Column(name = "longitude", nullable = false)
    private Double longitude; // 경도 == x축

    @Column(name = "latitude", nullable = false)
    private Double latitude; // 위도 == y축

    @OneToMany(mappedBy = "partnership", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnershipCollege> partnershipColleges = new ArrayList<>();

    @OneToMany(mappedBy = "partnership", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnershipDepartment> partnershipDepartments = new ArrayList<>();

    @OneToMany(mappedBy = "partnership", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnershipLike> likes = new ArrayList<>();
}
