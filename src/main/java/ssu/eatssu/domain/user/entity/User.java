package ssu.eatssu.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.partnership.entity.PartnershipLike;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewLike;
import ssu.eatssu.domain.user.department.entity.Department;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(unique = true)
	private String email;

	private String nickname;

	@Enumerated(EnumType.STRING)
	private OAuthProvider provider;

	private String providerId;

	private String credentials;

	@Enumerated(EnumType.STRING)
	private UserStatus status;

	@OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE
		, CascadeType.REFRESH})
	private List<Review> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
	private List<Report> reviewReports = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
	private List<Inquiry> userInquiries = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = {CascadeType.ALL}, orphanRemoval = true)
	private List<ReviewLike> reviewLikes = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;

	@OneToMany(mappedBy = "user", cascade = {CascadeType.ALL}, orphanRemoval = true)
	private List<PartnershipLike> partnershipLikes = new ArrayList<>();

	/**
	 * Oauth 회원가입 용 생성자
	 */
	private User(@NotNull String email, String nickname, @NotNull Role role, @NotNull OAuthProvider provider,
		@NotNull String providerId, @NotNull UserStatus status, @NotNull String credentials) {
		this.email = email;
		this.nickname = nickname;
		this.role = role;
		this.provider = provider;
		this.providerId = providerId;
		this.status = status;
		this.credentials = credentials;
	}

	/**
	 * <--Static Factory Method-->
	 * Oauth 회원가입
	 */
	public static User create(@NotNull String email, @NotNull String nickname, @NotNull OAuthProvider provider,
		String providerId,
		String credentials) {
		return new User(email, nickname, Role.USER, provider, providerId, UserStatus.ACTIVE, credentials);
	}

	/**
	 * <--Static Factory Method-->
	 * admin 회원가입
	 * Role 은 다른 방법으로 세팅할 예정
	 */
	public static User adminJoin(@NotNull String loginId, @NotNull String credentials) {
		return new User(loginId, null, Role.USER, OAuthProvider.EATSSU, loginId, UserStatus.INACTIVE, credentials);
	}

	public void updateNickname(@NotNull String nickname) {
		this.nickname = nickname;
	}

	public void updateEmail(String email) {
		this.email = email;
	}

	public void updateDepartment(Department department) {
		this.department = department;
	}

}
