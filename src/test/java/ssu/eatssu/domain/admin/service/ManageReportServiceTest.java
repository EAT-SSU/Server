package ssu.eatssu.domain.admin.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.admin.persistence.LoadReportRepository;
import ssu.eatssu.domain.admin.persistence.ManageReportRepository;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManageReportServiceTest {

    @Test
    void 리뷰와_연관된_신고를_일괄_삭제한다() {
        LoadReportRepository loader = mock(LoadReportRepository.class);
        ManageReportRepository repository = mock(ManageReportRepository.class);
        when(loader.findAllByReviewId(1L)).thenReturn(List.of(2L, 3L));

        new ManageReportService(loader, repository).deleteAllByReviewId(1L);

        verify(repository).deleteAllByIdInBatch(List.of(2L, 3L));
    }
}
