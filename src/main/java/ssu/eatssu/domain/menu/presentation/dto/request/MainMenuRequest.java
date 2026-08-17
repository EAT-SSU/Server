package ssu.eatssu.domain.menu.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "대표메뉴(영어 UI 노출용)")
public record MainMenuRequest(
        @Schema(description = "한글 메뉴명. menuNames의 항목과 정확히 일치해야 함", example = "돈까스")
        String nameKo,
        @Schema(description = "영문 메뉴명", example = "Pork Cutlet")
        String nameEn
) {

}
