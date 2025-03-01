package ssu.eatssu.domain.slice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class SliceResponse<D> {
    @Schema(description = "현재 넘겨준 페이지에 넘어간 개수(마지막 페이지일시, size 보다 작을 수 있음)", example = "20")
    private int numberOfElements;

    @Schema(description = "다음페이지가 있는지 알려주는 값(마지막 페이지라면 false)", example = "true")
    private boolean hasNext;

    @Schema(description = "데이터 리스트")
    private List<D> dataList;

    public static <T> SliceResponse<T> empty() {
        return new SliceResponse<>(0, false, Collections.emptyList());
    }
}
