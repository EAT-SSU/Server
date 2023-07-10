package ssu.eatssu.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum ReviewTag {
    GOOD("가성비가 좋아요"),
    BAD("다른데서 먹을래요"),
    FULL("든든한 한끼"),
    SOSO("그럭저럭해요"),
    SAD("아쉬워요"),
    SPICY("매워요"),
    BLAND("싱거워요"),
    SALTY("제 입맛엔 짜요"),
    BIG("양이 많아요"),
    FAST("빠르게 먹을 수 있어요");

    private String krName;

    ReviewTag(String krName){
        this.krName = krName;
    }

    @JsonCreator
    public static ReviewTag from(String s){
        return ReviewTag.valueOf(s.toUpperCase(Locale.ROOT));
    }


}
