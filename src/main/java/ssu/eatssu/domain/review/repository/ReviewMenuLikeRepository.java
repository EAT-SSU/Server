package ssu.eatssu.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewMenuLike;

import java.util.List;

public interface ReviewMenuLikeRepository extends JpaRepository<ReviewMenuLike, Long> {
    List<ReviewMenuLike> findByReview(Review review);
}
