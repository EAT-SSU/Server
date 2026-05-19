package ssu.eatssu.domain.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

class MyPageResponseTest {

    @Test
    void shouldReturnLocalizedNamesBasedOnUserLanguage() {
        // given
        College college = new College("IT 대학");
        ReflectionTestUtils.setField(college, "nameEn", "College of IT");

        Department department = new Department("컴퓨터학부");
        ReflectionTestUtils.setField(department, "nameEn", "School of Computer Science");
        ReflectionTestUtils.setField(department, "college", college);

        User user = User.create("test@test.com", "tester", OAuthProvider.EATSSU, "1234", "credentials");
        user.updateLanguage(Language.EN);
        user.updateDepartment(department);

        // when
        MyPageResponse response = MyPageResponse.from(user);

        // then
        assertThat(response.getLanguage()).isEqualTo(Language.EN);
        assertThat(response.getDepartmentName()).isEqualTo("School of Computer Science");
        assertThat(response.getCollegeName()).isEqualTo("College of IT");
    }
}
