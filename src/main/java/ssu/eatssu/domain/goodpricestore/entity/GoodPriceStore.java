package ssu.eatssu.domain.goodpricestore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodPriceStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private Integer sourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "main_menu")
    private String mainMenu;

    private Integer price;

    @Column(name = "road_address", nullable = false)
    private String roadAddress;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "image_url_1", length = 2048)
    private String imageUrl1;

    @Column(name = "image_url_2", length = 2048)
    private String imageUrl2;

    @Column(name = "image_url_3", length = 2048)
    private String imageUrl3;

    @Builder
    private GoodPriceStore(Integer sourceId, CategoryType category, String storeName, String mainMenu,
                            Integer price, String roadAddress, String district,
                            Double latitude, Double longitude,
                            String imageUrl1, String imageUrl2, String imageUrl3) {
        this.sourceId = sourceId;
        this.category = category;
        this.storeName = storeName;
        this.mainMenu = mainMenu;
        this.price = price;
        this.roadAddress = roadAddress;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
    }

    public String getRepresentativeImageUrl() {
        if (imageUrl1 != null) {
            return imageUrl1;
        }
        if (imageUrl2 != null) {
            return imageUrl2;
        }
        return imageUrl3;
    }
}
