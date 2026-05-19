package ssu.eatssu.global.i18n;

import ssu.eatssu.domain.user.entity.Language;

public interface Localizable {

    default String getLocalizedValue(Language language, String ko, String en, String ja, String vi) {
        if (language == null) {
            return ko;
        }

        return switch (language) {
            case EN -> en != null ? en : ko;
            case JA -> ja != null ? ja : ko;
            case VI -> vi != null ? vi : ko;
            case KO -> ko;
        };
    }
}
