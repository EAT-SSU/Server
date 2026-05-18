package ssu.eatssu.domain.user.department.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.user.entity.Language;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class College {
    @OneToMany(mappedBy = "college", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Department> departments = new ArrayList<>();
    @OneToMany(mappedBy = "partnershipCollege", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Partnership> partnerships = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "college_id")
    private Long id;
    @Column(name = "name_ko", nullable = false, unique = true)
    private String nameKo;
    @Column(name = "name_en")
    private String nameEn;
    @Column(name = "name_ja")
    private String nameJa;
    @Column(name = "name_vi")
    private String nameVi;

    public College(String name) {
        this.nameKo = name;
    }

    public String getName() {
        return nameKo;
    }

    public String getNameByLanguage(Language language) {
        if (language == null) {
            return nameKo;
        }

        return switch (language) {
            case EN -> nameEn != null ? nameEn : nameKo;
            case JA -> nameJa != null ? nameJa : nameKo;
            case VI -> nameVi != null ? nameVi : nameKo;
            case KO -> nameKo;
        };
    }
}
