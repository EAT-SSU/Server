package ssu.eatssu.domain.slice.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SliceResponseTest {

    @Test
    void emptyReturnsSliceResponseWithNoElements() {
        SliceResponse<String> response = SliceResponse.empty();

        assertThat(response.getNumberOfElements()).isZero();
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.getDataList()).isEmpty();
    }
}
