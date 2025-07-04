package ssu.eatssu.domain.slack.entity;

import lombok.Getter;

public enum SlackChannel {

    REPORT_CHANNEL("#신고"),
    ADDMENU_CHANNEL("#메뉴_추가"),
    ERROR_CHANNEL("#장애"),
    USER_INQUIRY_CHANNEL("#유저-문의"),
    SERVER_ERROR("C092J4J6F0U");

    @Getter
    private final String krName;

    SlackChannel(String krName) {
        this.krName = krName;
    }
}
