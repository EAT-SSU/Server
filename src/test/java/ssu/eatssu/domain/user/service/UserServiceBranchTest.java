package ssu.eatssu.domain.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.util.RandomNicknameUtil;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.department.persistence.CollegeRepository;
import ssu.eatssu.domain.user.department.persistence.DepartmentRepository;
import ssu.eatssu.domain.user.dto.request.LanguageUpdateRequest;
import ssu.eatssu.domain.user.dto.request.UpdateDepartmentRequest;
import ssu.eatssu.domain.user.dto.response.DepartmentResponse;
import ssu.eatssu.domain.user.dto.response.LanguageResponse;
import ssu.eatssu.domain.user.dto.response.MyPageResponse;
import ssu.eatssu.domain.user.entity.DeviceType;
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
import static org.mockito.ArgumentMatchers.any;

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

    @Test
    void 가입시_암호화된_자격증명과_랜덤닉네임을_저장한다() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        RandomNicknameUtil nicknameUtil = mock(RandomNicknameUtil.class);
        when(passwordEncoder.encode("KAKAOprovider-id")).thenReturn("encoded");
        when(nicknameUtil.generate()).thenReturn("랜덤닉네임");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserService service = new UserService(userRepository, passwordEncoder, mock(DepartmentRepository.class),
                mock(CollegeRepository.class), mock(ApplicationEventPublisher.class), nicknameUtil, mock(NicknameValidator.class));

        User user = service.join("user@eatssu.com", OAuthProvider.KAKAO, "provider-id");

        assertThat(user.getNickname()).isEqualTo("랜덤닉네임");
        assertThat(user.getCredentials()).isEqualTo("encoded");
    }

    @Test
    void 중복된_닉네임으로_변경할수_없다() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = mock(User.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("중복닉네임")).thenReturn(true);
        NicknameValidator validator = mock(NicknameValidator.class);
        UserService service = new UserService(userRepository, mock(PasswordEncoder.class), mock(DepartmentRepository.class),
                mock(CollegeRepository.class), mock(ApplicationEventPublisher.class), mock(RandomNicknameUtil.class), validator);

        assertThatThrownBy(() -> service.updateNickname(userDetails(), new ssu.eatssu.domain.user.dto.request.NicknameUpdateRequest("중복닉네임")))
                .isInstanceOf(BaseException.class);
        verify(validator).validateNickname("중복닉네임");
    }

    @Test
    void 회원탈퇴시_사용자를_삭제하고_이벤트를_발행한다() {
        UserRepository userRepository = mock(UserRepository.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        User user = User.create("user@eatssu.com", "닉네임", OAuthProvider.KAKAO, "provider-id", "credentials");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserService service = service(userRepository, mock(DepartmentRepository.class), mock(CollegeRepository.class), publisher);

        assertThat(service.withdraw(userDetails())).isTrue();
        verify(userRepository).delete(user);
        verify(publisher).publishEvent((Object) any());
    }

    @Test
    void V2로_가입시_디바이스타입을_저장한다() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        RandomNicknameUtil nicknameUtil = mock(RandomNicknameUtil.class);
        when(passwordEncoder.encode("KAKAOprovider-id")).thenReturn("encoded");
        when(nicknameUtil.generate()).thenReturn("랜덤닉네임");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserService service = new UserService(userRepository, passwordEncoder, mock(DepartmentRepository.class),
                mock(CollegeRepository.class), mock(ApplicationEventPublisher.class), nicknameUtil, mock(NicknameValidator.class));

        User user = service.joinV2("user@eatssu.com", OAuthProvider.KAKAO, "provider-id", DeviceType.ANDROID);

        assertThat(user.getDeviceType()).isEqualTo(DeviceType.ANDROID);
    }

    @Test
    void 존재하지_않는_학과는_등록할수_없다() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = mock(User.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
        when(departmentRepository.findById(3L)).thenReturn(Optional.empty());
        UserService service = service(userRepository, departmentRepository, mock(CollegeRepository.class),
                mock(ApplicationEventPublisher.class));

        assertThatThrownBy(() -> service.registerDepartment(new UpdateDepartmentRequest(3L), userDetails()))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 학과를_등록한다() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = mock(User.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
        Department department = mock(Department.class);
        when(departmentRepository.findById(3L)).thenReturn(Optional.of(department));
        UserService service = service(userRepository, departmentRepository, mock(CollegeRepository.class),
                mock(ApplicationEventPublisher.class));

        service.registerDepartment(new UpdateDepartmentRequest(3L), userDetails());

        verify(user).updateDepartment(department);
    }

    @Test
    void 마이페이지를_조회한다() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = mock(User.class);
        when(user.getNickname()).thenReturn("닉네임");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserService service = service(userRepository, mock(DepartmentRepository.class), mock(CollegeRepository.class),
                mock(ApplicationEventPublisher.class));

        MyPageResponse response = service.findMyPage(userDetails());

        assertThat(response.getNickname()).isEqualTo("닉네임");
    }

    @Test
    void 언어설정을_조회한다() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = mock(User.class);
        when(user.getLanguage()).thenReturn(Language.VI);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserService service = service(userRepository, mock(DepartmentRepository.class), mock(CollegeRepository.class),
                mock(ApplicationEventPublisher.class));

        LanguageResponse response = service.findLanguage(userDetails());

        assertThat(response.language()).isEqualTo(Language.VI);
    }

    @Test
    void 학과_정보를_조회한다() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = mock(User.class);
        Department department = mock(Department.class);
        when(user.getDepartment()).thenReturn(department);
        when(user.getLanguage()).thenReturn(Language.KO);
        when(department.getId()).thenReturn(1L);
        when(department.getNameByLanguage(Language.KO)).thenReturn("컴퓨터학부");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserService service = service(userRepository, mock(DepartmentRepository.class), mock(CollegeRepository.class),
                mock(ApplicationEventPublisher.class));

        DepartmentResponse response = service.getDepartment(userDetails());

        assertThat(response.departmentName()).isEqualTo("컴퓨터학부");
    }

    @Test
    void 사용가능한_닉네임인지_검증한다() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.existsByNickname("새닉네임")).thenReturn(false);
        NicknameValidator validator = mock(NicknameValidator.class);
        UserService service = new UserService(userRepository, mock(PasswordEncoder.class), mock(DepartmentRepository.class),
                mock(CollegeRepository.class), mock(ApplicationEventPublisher.class), mock(RandomNicknameUtil.class), validator);

        assertThat(service.validateNickname("새닉네임")).isTrue();
        verify(validator).validateNickname("새닉네임");
    }

    @Test
    void 로그인한_유저는_본인_언어로_학과_목록을_조회한다() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = mock(User.class);
        when(user.getLanguage()).thenReturn(Language.EN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        CollegeRepository collegeRepository = mock(CollegeRepository.class);
        College college = mock(College.class);
        when(college.getId()).thenReturn(1L);
        when(college.getNameByLanguage(Language.EN)).thenReturn("College of IT");
        when(collegeRepository.findById(1L)).thenReturn(Optional.of(college));
        DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
        Department department = mock(Department.class);
        when(department.getId()).thenReturn(2L);
        when(department.getNameByLanguage(Language.EN)).thenReturn("Computer Science");
        when(departmentRepository.findByCollege(college)).thenReturn(List.of(department));
        UserService service = service(userRepository, departmentRepository, collegeRepository,
                mock(ApplicationEventPublisher.class));

        var response = service.getDepartmentList(1L, userDetails());

        assertThat(response).extracting("name").containsExactly("Computer Science");
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", ssu.eatssu.domain.user.entity.Role.USER, null);
    }

    private UserService service(UserRepository userRepository, DepartmentRepository departmentRepository,
                                CollegeRepository collegeRepository, ApplicationEventPublisher publisher) {
        return new UserService(userRepository, mock(PasswordEncoder.class), departmentRepository, collegeRepository,
                               publisher, mock(RandomNicknameUtil.class), mock(NicknameValidator.class));
    }
}
