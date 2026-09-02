package ssu.eatssu.global.i18n;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.user.entity.Language;

import static org.assertj.core.api.Assertions.assertThat;

class LocalizableTest {

    private final Localizable localizable = new Localizable() { };

    @Test
    void 언어별_값을_반환하고_번역이_없으면_한국어로_대체한다() {
        assertThat(localizable.getLocalizedValue(Language.EN, "한글", "English", "日本語", "Tiếng Việt")).isEqualTo("English");
        assertThat(localizable.getLocalizedValue(Language.EN, "한글", null, "日本語", "Tiếng Việt")).isEqualTo("한글");
        assertThat(localizable.getLocalizedValue(Language.JA, "한글", "English", "日本語", "Tiếng Việt")).isEqualTo("日本語");
        assertThat(localizable.getLocalizedValue(Language.JA, "한글", "English", null, "Tiếng Việt")).isEqualTo("한글");
        assertThat(localizable.getLocalizedValue(Language.VI, "한글", "English", "日本語", "Tiếng Việt")).isEqualTo("Tiếng Việt");
        assertThat(localizable.getLocalizedValue(Language.VI, "한글", "English", "日本語", null)).isEqualTo("한글");
        assertThat(localizable.getLocalizedValue(Language.KO, "한글", "English", "日本語", "Tiếng Việt")).isEqualTo("한글");
        assertThat(localizable.getLocalizedValue(null, "한글", "English", "日本語", "Tiếng Việt")).isEqualTo("한글");
    }
}
