package ssu.eatssu.domain.inquiry.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.inquiry.dto.request.CreateInquiryRequest;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.inquiry.entity.InquiryStatus;
import ssu.eatssu.domain.inquiry.repository.InquiryRepository;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void 문의를_대기상태로_저장하고_이벤트를_발행한다() {
        UserRepository userRepository = mock(UserRepository.class);
        InquiryRepository inquiryRepository = mock(InquiryRepository.class);
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(inquiryRepository.save(any(Inquiry.class))).thenAnswer(invocation -> invocation.getArgument(0));
        InquiryService service = new InquiryService(userRepository, inquiryRepository, eventPublisher);

        Inquiry inquiry = service.createUserInquiry(new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null),
                new CreateInquiryRequest());

        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.WAITING);
        verify(eventPublisher).publishEvent(any(Object.class));
    }
}
