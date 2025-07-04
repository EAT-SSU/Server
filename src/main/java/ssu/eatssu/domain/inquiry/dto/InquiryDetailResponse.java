package ssu.eatssu.domain.inquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.inquiry.entity.Inquiry;

@AllArgsConstructor
@Builder
@Schema(title = "문의내역 상세")
@Getter
public class InquiryDetailResponse {

    @Schema(description = "문의 작성자 Id", example = "123")
    private Long writerId;

    @Schema(description = "문의 작성자 닉네임", example = "먹짱맨")
    private String writerNickName;

    @Schema(description = "문의 작성자 이메일", example = "test@gmail.com")
    private String writerEmail;

    @Schema(description = "문의 내용", example = "어쩌고 저쩌고 문의드립니다")
    private String content;

    public static InquiryDetailResponse from(Inquiry userInquiry) {
        return InquiryDetailResponse.builder()
                                    .writerId(userInquiry.getUser().getId())
                                    .writerNickName(userInquiry.getUser().getNickname())
                                    .writerEmail(userInquiry.getUser().getEmail())
                                    .content(userInquiry.getContent())
                                    .build();
    }
}
