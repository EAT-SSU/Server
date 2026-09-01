package ssu.eatssu.domain.admin.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ssu.eatssu.domain.admin.dto.response.PageWrapper;
import ssu.eatssu.domain.admin.dto.response.ReportLine;
import ssu.eatssu.domain.admin.persistence.LoadReportRepository;
import ssu.eatssu.domain.admin.persistence.ManageReportRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
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

    @Test
    void getReportBoardReturnsPagedReportLines() {
        LoadReportRepository loader = mock(LoadReportRepository.class);
        ReportLine reportLine = new ReportLine(1L, null, "기타", 2L, "리뷰내용");
        PageRequest pageable = PageRequest.of(0, 20);
        given(loader.findAll(pageable)).willReturn(new PageImpl<>(List.of(reportLine), pageable, 1));
        ManageReportService service = new ManageReportService(loader, mock(ManageReportRepository.class));

        PageWrapper<ReportLine> result = service.getReportBoard(pageable);

        assertThat(result.content()).containsExactly(reportLine);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void deleteRemovesReportById() {
        ManageReportRepository repository = mock(ManageReportRepository.class);
        ManageReportService service = new ManageReportService(mock(LoadReportRepository.class), repository);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }
}
