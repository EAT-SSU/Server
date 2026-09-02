package ssu.eatssu.domain.user.entity;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.user.department.entity.Department;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UserEntityTest {

    @Test
    void oauthFactoriesSetExpectedDefaults() {
        User user = User.createV2("user@eatssu.com", "닉네임", OAuthProvider.KAKAO, "provider", "credential",
                DeviceType.IOS);
        User admin = User.adminJoin("admin", "credential");

        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getDeviceType()).isEqualTo(DeviceType.IOS);
        assertThat(admin.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    void profileUpdateMethodsChangeUserState() {
        User user = User.create("old@email.com", "old", OAuthProvider.KAKAO, "id", "credential");
        Department department = mock(Department.class);

        user.updateNickname("new");
        user.updateEmail("new@email.com");
        user.updateLanguage(Language.EN);
        user.updateDeviceType(DeviceType.ANDROID);
        user.updateDepartment(department);

        assertThat(user.getNickname()).isEqualTo("new");
        assertThat(user.getEmail()).isEqualTo("new@email.com");
        assertThat(user.getLanguage()).isEqualTo(Language.EN);
        assertThat(user.getDeviceType()).isEqualTo(DeviceType.ANDROID);
        assertThat(user.getDepartment()).isSameAs(department);
    }
}
