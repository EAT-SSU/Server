package ssu.eatssu.domain.menu.entity.constants;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimePartTest {

    @Test
    void fromReturnsMatchingTimePartByDescription() {
        assertThat(TimePart.from("조식")).isEqualTo(TimePart.MORNING);
        assertThat(TimePart.from("중식")).isEqualTo(TimePart.LUNCH);
        assertThat(TimePart.from("석식")).isEqualTo(TimePart.DINNER);
    }

    @Test
    void fromThrowsWhenDescriptionIsUnknown() {
        assertThatThrownBy(() -> TimePart.from("존재하지않음"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
