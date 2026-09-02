package ssu.eatssu.domain.review.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MenuFilterUtilTest {

    @Test
    void isExcludedFromReviewReturnsTrueForNullOrBlank() {
        assertThat(MenuFilterUtil.isExcludedFromReview(null)).isTrue();
        assertThat(MenuFilterUtil.isExcludedFromReview("")).isTrue();
        assertThat(MenuFilterUtil.isExcludedFromReview("   ")).isTrue();
    }

    @Test
    void isExcludedFromReviewReturnsTrueForExactExcludedKeyword() {
        assertThat(MenuFilterUtil.isExcludedFromReview("쌀밥")).isTrue();
        assertThat(MenuFilterUtil.isExcludedFromReview("깍두기")).isTrue();
    }

    @Test
    void isExcludedFromReviewReturnsTrueForStandaloneKimchiToken() {
        assertThat(MenuFilterUtil.isExcludedFromReview("김치")).isTrue();
    }

    @Test
    void isExcludedFromReviewReturnsTrueForStandaloneBapToken() {
        assertThat(MenuFilterUtil.isExcludedFromReview("밥")).isTrue();
    }

    @Test
    void isExcludedFromReviewReturnsTrueForExactSauceMatch() {
        assertThat(MenuFilterUtil.isExcludedFromReview("소스")).isTrue();
    }

    @Test
    void isExcludedFromReviewReturnsFalseWhenSauceIsOnlyASuffix() {
        // matches()는 문자열 전체가 패턴과 일치해야 하므로 "소스"로 끝나기만 하는 이름은 제외되지 않는다
        assertThat(MenuFilterUtil.isExcludedFromReview("돈까스소스")).isFalse();
    }

    @Test
    void isExcludedFromReviewReturnsFalseForRegularMenuName() {
        assertThat(MenuFilterUtil.isExcludedFromReview("제육볶음")).isFalse();
    }

    @Test
    void isExcludedFromReviewReturnsTrueWhenEverySplitItemIsExcluded() {
        assertThat(MenuFilterUtil.isExcludedFromReview("쌀밥+깍두기")).isTrue();
    }

    @Test
    void isExcludedFromReviewReturnsFalseWhenAnySplitItemIsNotExcluded() {
        assertThat(MenuFilterUtil.isExcludedFromReview("제육볶음+쌀밥")).isFalse();
    }
}
