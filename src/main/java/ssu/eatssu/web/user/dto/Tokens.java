package ssu.eatssu.web.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Tokens {

    private String accessToken;
    private String refreshToken;

    @Builder
    public Tokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
