package ssu.eatssu.domain.partnership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.global.i18n.Localizable;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Where(clause = "end_date >= CURRENT_DATE")
public class Partnership implements Localizable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partnership_id")
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_ja", columnDefinition = "TEXT")
    private String descriptionJa;

    @Column(name = "description_vi", columnDefinition = "TEXT")
    private String descriptionVi;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false)
    private PeriodType periodType = PeriodType.NORMAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partnership_college_id")
    private College partnershipCollege;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partnership_department_id")
    private Department partnershipDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partnership_restaurant_id")
    private PartnershipRestaurant partnershipRestaurant;

    public String getDescriptionByLanguage(Language language) {
        return getLocalizedValue(language, description, descriptionEn, descriptionJa, descriptionVi);
    }

    public void setPartnershipCollege(College partnershipCollege) {
        this.partnershipCollege = partnershipCollege;
    }

    public void setPartnershipDepartment(Department partnershipDepartment) {
        this.partnershipDepartment = partnershipDepartment;
    }
}
