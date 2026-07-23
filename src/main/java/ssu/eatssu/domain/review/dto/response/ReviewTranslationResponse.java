package ssu.eatssu.domain.review.dto.response;

import ssu.eatssu.domain.user.entity.Language;

public record ReviewTranslationResponse(
        Long reviewId,
        Language language,
        String translatedContent,
        boolean cached
) {
}
