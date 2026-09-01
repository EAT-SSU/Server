package ssu.eatssu.domain.review.service;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewTranslation;
import ssu.eatssu.domain.review.infrastructure.DeepLTranslationClient;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.review.repository.ReviewTranslationRepository;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewTranslationServiceTest {

    @Test
    void 지원하지_않는_언어는_번역하지_않는다() {
        DeepLTranslationClient client = mock(DeepLTranslationClient.class);
        ReviewTranslationService service = service(mock(ReviewRepository.class), mock(ReviewTranslationRepository.class), client);

        assertThatThrownBy(() -> service.translateReview(1L, Language.KO)).isInstanceOf(BaseException.class);
        verify(client, never()).translate(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 존재하지_않는_리뷰은_번역하지_않는다() {
        ReviewRepository repository = mock(ReviewRepository.class);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service(repository, mock(ReviewTranslationRepository.class), mock(DeepLTranslationClient.class))
                .translateReview(1L, Language.EN)).isInstanceOf(BaseException.class);
    }

    @Test
    void 캐시된_번역을_반환한다() {
        ReviewRepository reviewRepository = mock(ReviewRepository.class);
        ReviewTranslationRepository translationRepository = mock(ReviewTranslationRepository.class);
        Review review = mock(Review.class);
        ReviewTranslation translation = ReviewTranslation.builder().review(review).language(Language.EN)
                                                             .translatedContent("translated").charCount(4).build();
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(translationRepository.findByReview_IdAndLanguage(1L, Language.EN)).thenReturn(Optional.of(translation));

        var response = service(reviewRepository, translationRepository, mock(DeepLTranslationClient.class)).translateReview(1L, Language.EN);

        assertThat(response.cached()).isTrue();
        assertThat(response.translatedContent()).isEqualTo("translated");
    }

    @Test
    void 리뷰_내용이_없으면_번역하지_않는다() {
        ReviewRepository reviewRepository = mock(ReviewRepository.class);
        ReviewTranslationRepository translationRepository = mock(ReviewTranslationRepository.class);
        Review review = mock(Review.class);
        when(review.getContent()).thenReturn("  ");
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(translationRepository.findByReview_IdAndLanguage(1L, Language.EN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service(reviewRepository, translationRepository, mock(DeepLTranslationClient.class))
                .translateReview(1L, Language.EN))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 번역을_수행하고_저장한다() {
        ReviewRepository reviewRepository = mock(ReviewRepository.class);
        ReviewTranslationRepository translationRepository = mock(ReviewTranslationRepository.class);
        DeepLTranslationClient client = mock(DeepLTranslationClient.class);
        Review review = mock(Review.class);
        when(review.getContent()).thenReturn("맛있어요");
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(translationRepository.findByReview_IdAndLanguage(1L, Language.EN)).thenReturn(Optional.empty());
        when(client.translate("맛있어요", Language.EN)).thenReturn("Delicious");
        TransactionTemplate transactionTemplate = executingTransactionTemplate();

        var response = new ReviewTranslationService(reviewRepository, translationRepository, client,
                transactionTemplate).translateReview(1L, Language.EN);

        assertThat(response.cached()).isFalse();
        assertThat(response.translatedContent()).isEqualTo("Delicious");
        verify(translationRepository).save(any(ReviewTranslation.class));
    }

    @Test
    void 중복_저장_예외는_무시한다() {
        ReviewRepository reviewRepository = mock(ReviewRepository.class);
        ReviewTranslationRepository translationRepository = mock(ReviewTranslationRepository.class);
        DeepLTranslationClient client = mock(DeepLTranslationClient.class);
        Review review = mock(Review.class);
        when(review.getContent()).thenReturn("맛있어요");
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(translationRepository.findByReview_IdAndLanguage(1L, Language.EN)).thenReturn(Optional.empty());
        when(client.translate("맛있어요", Language.EN)).thenReturn("Delicious");
        TransactionTemplate transactionTemplate = mock(TransactionTemplate.class);
        doThrow(new DataIntegrityViolationException("중복")).when(transactionTemplate).executeWithoutResult(any());

        assertThatCode(() -> new ReviewTranslationService(reviewRepository, translationRepository, client,
                transactionTemplate).translateReview(1L, Language.EN))
                .doesNotThrowAnyException();
    }

    private TransactionTemplate executingTransactionTemplate() {
        TransactionTemplate transactionTemplate = mock(TransactionTemplate.class);
        doAnswer(invocation -> {
            java.util.function.Consumer<org.springframework.transaction.TransactionStatus> callback =
                    invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
        return transactionTemplate;
    }

    private ReviewTranslationService service(ReviewRepository reviewRepository,
                                             ReviewTranslationRepository translationRepository,
                                             DeepLTranslationClient client) {
        return new ReviewTranslationService(reviewRepository, translationRepository, client, mock(TransactionTemplate.class));
    }
}
