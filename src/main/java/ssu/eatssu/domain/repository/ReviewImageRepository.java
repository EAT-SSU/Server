package ssu.eatssu.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.review.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
