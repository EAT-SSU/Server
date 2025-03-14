package ssu.eatssu.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewLike;
import ssu.eatssu.domain.user.entity.User;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
	Optional<ReviewLike> findByReviewAndUser(Review review, User user);
}
