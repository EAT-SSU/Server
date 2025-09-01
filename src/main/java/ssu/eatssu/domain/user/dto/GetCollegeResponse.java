package ssu.eatssu.domain.user.dto;

import lombok.Builder;

@Builder
public record GetCollegeResponse(
        Long id,
        String name
) {
}
