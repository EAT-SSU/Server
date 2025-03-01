package ssu.eatssu.domain.partnership.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Where(clause = "end_date >= CURRENT_DATE")
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

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "restaurant_type", nullable = false)
    private RestaurantType restaurantType;

    @Column(name = "longitude", nullable = false)
    private Double longitude; // 경도 == x축

    @Column(name = "latitude", nullable = false)
    private Double latitude; // 위도 == y축

    @Builder.Default
    @OneToMany(mappedBy = "partnership", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnershipCollege> partnershipColleges = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "partnership", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnershipDepartment> partnershipDepartments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "partnership", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnershipLike> likes = new ArrayList<>();
}
