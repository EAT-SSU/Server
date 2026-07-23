package ssu.eatssu.domain.user.dto.response;

import lombok.Builder;

@Builder
public record GetCollegeResponse(
        Long id,
        String name
) {
}
