package ssu.eatssu.domain.user.dto.response;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class UserProfileResponseTest {

    @Test
    void departmentResponseHandlesMissingDepartmentAndCollege() {
        assertThat(DepartmentResponse.from(null)).isEqualTo(new DepartmentResponse(null, null, null, null));

        Department department = mock(Department.class);
        given(department.getId()).willReturn(1L);
        given(department.getNameByLanguage(Language.EN)).willReturn("Computer Science");

        DepartmentResponse response = DepartmentResponse.from(department, Language.EN);

        assertThat(response.departmentName()).isEqualTo("Computer Science");
        assertThat(response.collegeId()).isNull();
    }

    @Test
    void departmentResponseUsesKoreanByDefault() {
        Department department = mock(Department.class);
        given(department.getId()).willReturn(1L);
        given(department.getNameByLanguage(Language.KO)).willReturn("컴퓨터학부");

        DepartmentResponse response = DepartmentResponse.from(department);

        assertThat(response.departmentName()).isEqualTo("컴퓨터학부");
    }

    @Test
    void myPageResponseMapsUserDepartmentAndCollege() {
        User user = mock(User.class);
        Department department = mock(Department.class);
        College college = mock(College.class);
        given(user.getNickname()).willReturn("닉네임");
        given(user.getProvider()).willReturn(OAuthProvider.KAKAO);
        given(user.getLanguage()).willReturn(Language.EN);
        given(user.getDepartment()).willReturn(department);
        given(department.getId()).willReturn(1L);
        given(department.getNameByLanguage(Language.EN)).willReturn("Computer Science");
        given(department.getCollege()).willReturn(college);
        given(college.getId()).willReturn(2L);
        given(college.getNameByLanguage(Language.EN)).willReturn("IT College");

        MyPageResponse response = MyPageResponse.from(user);

        assertThat(response.getNickname()).isEqualTo("닉네임");
        assertThat(response.getDepartmentName()).isEqualTo("Computer Science");
        assertThat(response.getCollegeName()).isEqualTo("IT College");
        assertThat(MyPageResponse.from(null).getNickname()).isNull();
    }

    @Test
    void myPageResponseHandlesMissingDepartmentAndCollege() {
        User user = mock(User.class);
        given(user.getNickname()).willReturn("닉네임");
        given(user.getDepartment()).willReturn(null);

        MyPageResponse response = MyPageResponse.from(user);

        assertThat(response.getDepartmentId()).isNull();
        assertThat(response.getCollegeId()).isNull();
    }

    @Test
    void myPageResponseHandlesDepartmentWithoutCollege() {
        User user = mock(User.class);
        Department department = mock(Department.class);
        given(user.getDepartment()).willReturn(department);
        given(department.getId()).willReturn(1L);
        given(department.getCollege()).willReturn(null);

        MyPageResponse response = MyPageResponse.from(user);

        assertThat(response.getDepartmentId()).isEqualTo(1L);
        assertThat(response.getCollegeId()).isNull();
        assertThat(response.getCollegeName()).isNull();
    }

    @Test
    void languageResponseMapsUserLanguage() {
        User user = mock(User.class);
        given(user.getLanguage()).willReturn(Language.JA);

        assertThat(LanguageResponse.from(user).language()).isEqualTo(Language.JA);
    }
}
