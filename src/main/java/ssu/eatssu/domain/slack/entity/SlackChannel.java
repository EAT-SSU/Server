package ssu.eatssu.domain.slack.entity;

import lombok.Getter;

public enum SlackChannel {

    REPORT_CHANNEL("#01-신고"),
    // TODO: 회의에서 제안 후 삭제한다
    USER_INQUIRY_CHANNEL("#유저-문의"),
    SERVER_ERROR("C092J4J6F0U");

    @Getter
    private final String krName;

    SlackChannel(String krName) {
        this.krName = krName;
    }
}
