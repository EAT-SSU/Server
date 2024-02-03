package ssu.eatssu.domain.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.admin.event.ReviewDeleteEvent;
import ssu.eatssu.domain.admin.persistence.ManageReviewRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ManageReviewService {
    private final ManageReviewRepository manageReviewRepository;
    private final ApplicationEventPublisher publisher;

    public void delete(Long reviewId) {
        // 리뷰 삭제하기 전에 해당 리뷰를 참조하는 신고들을 모두 삭제한다.
        publisher.publishEvent(new ReviewDeleteEvent(reviewId));

        manageReviewRepository.deleteById(reviewId);
    }
}
