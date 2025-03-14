package ssu.eatssu.domain.partnership.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.user.department.entity.College;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartnershipCollege {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "partnership_college_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "partnership_id")
	private Partnership partnership;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "college_id")
	private College college;

	public PartnershipCollege(Partnership partnership, College college) {
		this.partnership = partnership;
		this.college = college;
	}
}
