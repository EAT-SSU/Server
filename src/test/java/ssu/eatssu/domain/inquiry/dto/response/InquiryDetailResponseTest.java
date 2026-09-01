package ssu.eatssu.domain.inquiry.dto.response;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class InquiryDetailResponseTest {

    @Test
    void fromMapsInquiryWriterAndContent() {
        Inquiry inquiry = mock(Inquiry.class);
        User user = mock(User.class);
        given(inquiry.getUser()).willReturn(user);
        given(inquiry.getContent()).willReturn("문의 내용");
        given(user.getId()).willReturn(1L);
        given(user.getNickname()).willReturn("닉네임");
        given(user.getEmail()).willReturn("user@eatssu.com");

        InquiryDetailResponse response = InquiryDetailResponse.from(inquiry);

        assertThat(response.getWriterId()).isEqualTo(1L);
        assertThat(response.getWriterNickName()).isEqualTo("닉네임");
        assertThat(response.getWriterEmail()).isEqualTo("user@eatssu.com");
        assertThat(response.getContent()).isEqualTo("문의 내용");
    }
}
