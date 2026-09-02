package ssu.eatssu.domain.admin.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import ssu.eatssu.domain.admin.event.ReviewDeleteEvent;
import ssu.eatssu.domain.admin.persistence.ManageReviewRepository;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ManageReviewServiceTest {

    @Test
    void 리뷰_삭제_전에_신고_삭제_이벤트를_발행한다() {
        ManageReviewRepository repository = mock(ManageReviewRepository.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        new ManageReviewService(repository, publisher).delete(3L);

        verify(publisher).publishEvent(argThat((ReviewDeleteEvent event) -> event.reviewId().equals(3L)));
        verify(repository).deleteById(3L);
    }
}
