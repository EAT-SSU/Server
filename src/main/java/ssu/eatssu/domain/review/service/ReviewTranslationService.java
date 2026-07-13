package ssu.eatssu.domain.review.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ssu.eatssu.domain.review.dto.ReviewTranslationResponse;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewTranslation;
import ssu.eatssu.domain.review.infrastructure.DeepLTranslationClient;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.review.repository.ReviewTranslationRepository;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.global.handler.response.BaseException;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.FAILED_VALIDATION;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_REVIEW;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewTranslationService {

    private final ReviewRepository reviewRepository;
    private final ReviewTranslationRepository reviewTranslationRepository;
    private final DeepLTranslationClient deeplTranslationClient;
    private final TransactionTemplate transactionTemplate;

    public ReviewTranslationResponse translateReview(Long reviewId, Language language) {
        if (language != Language.EN) {
            throw new BaseException(FAILED_VALIDATION);
        }

        Review review = reviewRepository.findById(reviewId)
                                        .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));

        Optional<ReviewTranslation> cached = reviewTranslationRepository.findByReview_IdAndLanguage(reviewId,
                language);
        if (cached.isPresent()) {
            ReviewTranslation translation = cached.get();
            log.info("[리뷰 번역] reviewId={} language={} charCount={} cached=true", reviewId, language,
                    translation.getCharCount());
            return new ReviewTranslationResponse(reviewId, language, translation.getTranslatedContent(), true);
        }

        String content = review.getContent();
        if (content == null || content.isBlank()) {
            throw new BaseException(FAILED_VALIDATION);
        }

        String translatedContent = deeplTranslationClient.translate(content, language);
        int charCount = content.length();

        saveTranslation(review, language, translatedContent, charCount);

        log.info("[리뷰 번역] reviewId={} language={} charCount={} cached=false", reviewId, language, charCount);
        return new ReviewTranslationResponse(reviewId, language, translatedContent, false);
    }

    private void saveTranslation(Review review, Language language, String translatedContent, int charCount) {
        try {
            transactionTemplate.executeWithoutResult(status ->
                    reviewTranslationRepository.save(ReviewTranslation.builder()
                                                                       .review(review)
                                                                       .language(language)
                                                                       .translatedContent(translatedContent)
                                                                       .charCount(charCount)
                                                                       .build()));
        } catch (DataIntegrityViolationException e) {
            log.warn("[리뷰 번역] 캐시 중복 저장 감지 reviewId={} language={}", review.getId(), language);
        }
    }
}
