package ssu.eatssu.domain.report.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum ReportType {
    NO_ASSOCIATE_CONTENT("메뉴와 관련 없는 내용"),
    IMPROPER_CONTENT("음란성, 욕설 등 부적절한 내용"),
    IMPROPER_ADVERTISEMENT("부적절한 홍보 및 광고"),
    COPY("리뷰 취지에 맞지 않는 행동(복사글 등)"),
    COPYRIGHT("저작권 도용 의심(사진 등)"),
    EXTRA("기타 (하단 내용 작성)");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    @JsonCreator
    public static ReportType from(final String name) {
        return ReportType.valueOf(name.toUpperCase(Locale.ROOT));
    }
}
