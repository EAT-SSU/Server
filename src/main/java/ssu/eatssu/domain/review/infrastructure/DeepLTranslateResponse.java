package ssu.eatssu.domain.review.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DeepLTranslateResponse(List<Translation> translations) {

    public record Translation(
            @JsonProperty("detected_source_language") String detectedSourceLanguage,
            String text
    ) {
    }
}
