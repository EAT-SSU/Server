package ssu.eatssu.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ssu.eatssu.domain.review.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
