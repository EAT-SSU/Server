package ssu.eatssu.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "jwt 토큰들")
public record Tokens(
        @Schema(description = "accessToken", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOjEsXCJlbWFpbFwiOlwiZW1haWxAZW1haWwuY29tXCJ9IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTY3OTIyNzc0NX0.gM7F00Dh7OvybtEYODxYqFNATDDdquGCIivAeifNrEnF1ush3Fx1ChWqwD60U6Ek7rmJRU3CUUFAMLUrWDi4Aw")
        String accessToken,

        @Schema(description = "refreshToken", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOjEsXCJlbWFpbFwiOlwiZW1haWxAZW1haWwuY29tXCJ9IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTY3OTgzMDc0NX0.OBtrGGKuSujBxwLTNVs-sc4eEH8uYiG8-Cwomqb_OgB9ADVbWbtSqaml9Ll34TFrKhPZuhMvzdchsWHqMQQ_kg")
        String refreshToken
) {
}
