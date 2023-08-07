package ssu.eatssu.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum ReportStatus {
    PENDING ("대기 중"),
    IN_PROGRESS ("진행 중"),
    RESOLVED ("해결됨"),
    REJECTED ("거절됨"),
    FALSE_REPORT ("거짓 신고");

    private final String krName;

    ReportStatus(String krName){
        this.krName = krName;
    }

    @JsonCreator
    public static ReportStatus from(String s){
        return ReportStatus.valueOf(s.toUpperCase(Locale.ROOT));
    }
}
