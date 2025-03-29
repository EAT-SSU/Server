package ssu.eatssu.domain.review.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.user.entity.BaseTimeEntity;
import ssu.eatssu.domain.user.entity.User;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Review extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long id;

	private String content;

	// TODO : 삭제되어야 함
	@Embedded
	private Ratings ratings;

	private Integer rating;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	// TODO : 삭제되어야 함
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id")
	private Menu menu;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meal_id")
	private Meal meal;

	@Builder.Default
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewImage> reviewImages = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewMenuLike> menuLikes = new ArrayList<>();

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewLike> reviewLikes = new ArrayList<>();

	public void update(String content, Integer mainRate, Integer amountRate, Integer tasteRate) {
		this.content = content;
		this.ratings = Ratings.of(mainRate, amountRate, tasteRate);
	}

	// TODO : this.user가 null이면?
	public boolean isNotWrittenBy(User user) {
		return !this.user.equals(user);
	}

	public void clearUser() {
		this.user = null;
	}

	public void addReviewImage(String imageUrl) {
		ReviewImage reviewImage = new ReviewImage(this, imageUrl);
		this.reviewImages.add(reviewImage);
	}

	public void addReviewMenuLike(Menu menu, boolean isLike) {
		ReviewMenuLike reviewMenuLike = ReviewMenuLike.create(this, menu, isLike);
		this.menuLikes.add(reviewMenuLike);

		if (isLike) {
			menu.increaseLikeCount();
		} else {
			menu.increaseUnlikeCount();
		}
	}

	public void removeReviewMenuLike(ReviewMenuLike reviewMenuLike) {
		this.menuLikes.remove(reviewMenuLike);

		if (reviewMenuLike.getIsLike()) {
			reviewMenuLike.getMenu().decreaseLikeCount();
		} else {
			reviewMenuLike.getMenu().decreaseUnlikeCount();
		}
	}

	public void update(String content, int rating, Map<Menu, Boolean> updatedMenuLikes) {
		this.content = content;
		this.rating = rating;

		// 현재 menu like 상태를 menu를 기준으로 매핑
		Map<Menu, ReviewMenuLike> currentMenuLikes = this.menuLikes.stream()
																   .collect(Collectors.toMap(ReviewMenuLike::getMenu,
																	   menuLike -> menuLike));

		for (Map.Entry<Menu, Boolean> entry : updatedMenuLikes.entrySet()) {
			Menu menu = entry.getKey();
			Boolean newState = entry.getValue();
			ReviewMenuLike existingReviewMenuLike = currentMenuLikes.get(menu);
			if (existingReviewMenuLike == null) {
				// 기존에 없는 메뉴이면 새롭게 추가
				this.addReviewMenuLike(menu, newState);
			} else {
				// 기존에 있는 경우 : 상태가 다르면 업데이트
				if (!existingReviewMenuLike.getIsLike().equals(newState)) {
					existingReviewMenuLike.updateLike(newState);
					menu.changeLikeStatus(newState);
				}
			}
		}
	}

	public void resetMenuLikes() {
		for (ReviewMenuLike reviewMenuLike : this.menuLikes) {
			reviewMenuLike.resetMenuLikeStatus();
		}
		this.menuLikes.clear();
	}
}
