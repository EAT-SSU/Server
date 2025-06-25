package ssu.eatssu.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(title = "유효한 토큰 확인")
public record ValidRequest(
        @NotBlank(message = "토큰을 입력해주세요")
        @Schema(description = "토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOjcsXCJlbWFpbFwiOlwidGVzdEBlbWFpbC5jb21cIixcInJvbGVcIjpcIlJPTEVfVVNFUlwifSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE3NDQzNzQ0MjB9.mhIWYX_Vj3xW1eXuVflbzpH6vLTcC9b1twbIcqovVjDVnS7tjegu3nQHGXUsUa_WG2DIAtJMFZT_Q1XcVq1jPw")
        String token
) {

}
