package ssu.eatssu.domain.inquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "문의 남기기")
@NoArgsConstructor
@Getter
public class UserInquiryCreate {
    @Schema(description = "문의 내용", example = "어쩌고 저쩌고 문의 남깁니다")
    private String content;
}
