package ssu.eatssu.domain.review.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.review.entity.ReviewTranslation;
import ssu.eatssu.domain.user.entity.Language;

public interface ReviewTranslationRepository extends JpaRepository<ReviewTranslation, Long> {

    Optional<ReviewTranslation> findByReview_IdAndLanguage(Long reviewId, Language language);

    void deleteAllByReview_Id(Long reviewId);
}
