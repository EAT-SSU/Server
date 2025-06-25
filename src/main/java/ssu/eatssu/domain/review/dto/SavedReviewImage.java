package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "업로드 된 이미지 url")
public class SavedReviewImage {

    @Schema(description = "이미지 url", example = "image123.jpg")
    private String url;
}
