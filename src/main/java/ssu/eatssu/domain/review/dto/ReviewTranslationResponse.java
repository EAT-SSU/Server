package ssu.eatssu.domain.review.dto;

import ssu.eatssu.domain.user.entity.Language;

public record ReviewTranslationResponse(
        Long reviewId,
        Language language,
        String translatedContent,
        boolean cached
) {
}
