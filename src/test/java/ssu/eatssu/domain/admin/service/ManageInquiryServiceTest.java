package ssu.eatssu.domain.admin.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ssu.eatssu.domain.admin.dto.request.UpdateStatusRequest;
import ssu.eatssu.domain.admin.dto.response.PageWrapper;
import ssu.eatssu.domain.admin.dto.response.InquiryLine;
import ssu.eatssu.domain.admin.persistence.LoadInquiryRepository;
import ssu.eatssu.domain.admin.persistence.ManageInquiryRepository;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.inquiry.entity.InquiryStatus;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ManageInquiryServiceTest {

    @Test
    void updateStatusUpdatesAndSavesInquiry() {
        ManageInquiryRepository repository = mock(ManageInquiryRepository.class);
        Inquiry inquiry = Inquiry.builder().status(InquiryStatus.WAITING).build();
        given(repository.findById(1L)).willReturn(Optional.of(inquiry));
        ManageInquiryService service = new ManageInquiryService(mock(LoadInquiryRepository.class), repository);

        service.updateStatus(1L, new UpdateStatusRequest(InquiryStatus.ANSWERED));

        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.ANSWERED);
        verify(repository).save(inquiry);
    }

    @Test
    void updateStatusThrowsWhenInquiryDoesNotExist() {
        ManageInquiryRepository repository = mock(ManageInquiryRepository.class);
        given(repository.findById(1L)).willReturn(Optional.empty());
        ManageInquiryService service = new ManageInquiryService(mock(LoadInquiryRepository.class), repository);

        assertThatThrownBy(() -> service.updateStatus(1L, new UpdateStatusRequest(InquiryStatus.ANSWERED)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void getInquiryBoardReturnsPagedInquiryLines() {
        LoadInquiryRepository loadRepository = mock(LoadInquiryRepository.class);
        Inquiry inquiry = Inquiry.builder().id(1L).email("test@test.com").content("문의").status(InquiryStatus.WAITING)
                                 .build();
        PageRequest pageable = PageRequest.of(0, 20);
        given(loadRepository.findAllInquiries(pageable)).willReturn(new PageImpl<>(java.util.List.of(inquiry), pageable, 1));
        ManageInquiryService service = new ManageInquiryService(loadRepository, mock(ManageInquiryRepository.class));

        PageWrapper<InquiryLine> result = service.getInquiryBoard(pageable);

        assertThat(result.content()).extracting(InquiryLine::inquiryId).containsExactly(1L);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void deleteRemovesInquiryById() {
        ManageInquiryRepository repository = mock(ManageInquiryRepository.class);
        ManageInquiryService service = new ManageInquiryService(mock(LoadInquiryRepository.class), repository);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }
}
