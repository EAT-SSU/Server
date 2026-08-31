package ssu.eatssu.domain.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.auth.util.RandomNicknameUtil;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.persistence.CollegeRepository;
import ssu.eatssu.domain.user.department.persistence.DepartmentRepository;
import ssu.eatssu.domain.user.dto.request.LanguageUpdateRequest;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.user.util.NicknameValidator;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceBranchTest {

    @Test
    void 비회원은_한국어로_대학_목록을_조회한다() {
        CollegeRepository collegeRepository = mock(CollegeRepository.class);
        College college = mock(College.class);
        when(college.getId()).thenReturn(1L);
        when(college.getNameByLanguage(Language.KO)).thenReturn("숭실대학교");
        when(collegeRepository.findAll()).thenReturn(List.of(college));

        var response = service(mock(UserRepository.class), mock(DepartmentRepository.class), collegeRepository,
                               mock(ApplicationEventPublisher.class)).getCollegeList(null);

        assertThat(response).extracting("name").containsExactly("숭실대학교");
    }

    @Test
    void 없는_대학의_학과는_조회하지_않는다() {
        CollegeRepository collegeRepository = mock(CollegeRepository.class);
        when(collegeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service(mock(UserRepository.class), mock(DepartmentRepository.class), collegeRepository,
                                         mock(ApplicationEventPublisher.class)).getDepartmentList(1L, null))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 언어를_변경하고_이벤트를_발행한다() {
        UserRepository userRepository = mock(UserRepository.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        User user = mock(User.class);
        when(userDetails.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service(userRepository, mock(DepartmentRepository.class), mock(CollegeRepository.class), publisher)
                .updateLanguage(userDetails, new LanguageUpdateRequest(Language.EN));

        verify(user).updateLanguage(Language.EN);
        verify(publisher).publishEvent((Object) org.mockito.ArgumentMatchers.any());
    }

    private UserService service(UserRepository userRepository, DepartmentRepository departmentRepository,
                                CollegeRepository collegeRepository, ApplicationEventPublisher publisher) {
        return new UserService(userRepository, mock(PasswordEncoder.class), departmentRepository, collegeRepository,
                               publisher, mock(RandomNicknameUtil.class), mock(NicknameValidator.class));
    }
}
