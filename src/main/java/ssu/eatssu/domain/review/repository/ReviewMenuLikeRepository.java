package ssu.eatssu.domain.review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewMenuLike;

public interface ReviewMenuLikeRepository extends JpaRepository<ReviewMenuLike, Long> {
    List<ReviewMenuLike> findByReview(Review review);
}
