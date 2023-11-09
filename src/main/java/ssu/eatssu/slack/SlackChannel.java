package ssu.eatssu.slack;

import lombok.Getter;

public enum SlackChannel {

    REPORT_CHANNEL("#신고"), ADDMENU_CHANNEL("#메뉴_추가"), ERROR_CHANNEL("#장애");

    @Getter
    private String krName;

    SlackChannel(String krName){
        this.krName = krName;
    }
}
