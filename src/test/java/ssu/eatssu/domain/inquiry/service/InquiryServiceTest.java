package ssu.eatssu.domain.inquiry.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.inquiry.dto.request.CreateInquiryRequest;
import ssu.eatssu.domain.inquiry.repository.InquiryRepository;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InquiryServiceTest {

    @Test
    void 존재하지_않는_사용자의_문의는_생성하지_않는다() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        InquiryService service = new InquiryService(userRepository, mock(InquiryRepository.class),
                                                    mock(ApplicationEventPublisher.class));
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);

        assertThatThrownBy(() -> service.createUserInquiry(userDetails, new CreateInquiryRequest()))
                .isInstanceOf(BaseException.class);
    }
}
