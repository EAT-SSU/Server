package ssu.eatssu.domain.auth.dto;

import org.jetbrains.annotations.NotNull;

public record OAuthInfo(@NotNull String email, @NotNull String providerId) {

}