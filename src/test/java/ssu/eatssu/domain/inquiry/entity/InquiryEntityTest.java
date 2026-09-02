package ssu.eatssu.domain.inquiry.entity;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

class InquiryEntityTest {

    @Test
    void constructorInitializesWaitingStatus() {
        User user = User.create("test@test.com", "user-test", OAuthProvider.EATSSU, "1234", "1234");

        Inquiry inquiry = new Inquiry("문의합니다", user, "test@test.com");

        assertThat(inquiry.getContent()).isEqualTo("문의합니다");
        assertThat(inquiry.getUser()).isSameAs(user);
        assertThat(inquiry.getEmail()).isEqualTo("test@test.com");
        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.WAITING);
        assertThat(inquiry.getId()).isNull();
    }

    @Test
    void updateStatusChangesStatus() {
        Inquiry inquiry = new Inquiry("문의합니다", null, "test@test.com");

        inquiry.updateStatus(InquiryStatus.ANSWERED);

        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.ANSWERED);
    }

    @Test
    void clearUserRemovesUserReference() {
        User user = User.create("test@test.com", "user-test", OAuthProvider.EATSSU, "1234", "1234");
        Inquiry inquiry = new Inquiry("문의합니다", user, "test@test.com");

        inquiry.clearUser();

        assertThat(inquiry.getUser()).isNull();
    }
}
