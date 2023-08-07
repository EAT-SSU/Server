package ssu.eatssu.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum ReviewReportType {
    CONTENT("메뉴와 관련없는 내용"),
    BAD_WORD("음란성, 욕설 등 부적절한 내용"),
    AD("부적절한 홍보 및 광고"),
    COPY("리뷰 취지에 맞지 않는 행동(복사글 등)"),
    COPYRIGHT("저작권 도용 의심(사진 등)"),
    ETC("기타 (하단 내용 작성)");

    private final String krName;

    ReviewReportType(String krName){
        this.krName = krName;
    }

    @JsonCreator
    public static ReviewReportType from(String s){
        return ReviewReportType.valueOf(s.toUpperCase(Locale.ROOT));
    }
}