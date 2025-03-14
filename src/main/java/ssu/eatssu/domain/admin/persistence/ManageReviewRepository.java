package ssu.eatssu.domain.admin.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import ssu.eatssu.domain.review.entity.Review;

public interface ManageReviewRepository extends JpaRepository<Review, Long> {

}