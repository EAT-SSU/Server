package ssu.eatssu.domain.user.dto.response;

import lombok.Builder;

@Builder
public record GetDepartmentResponse(Long id,
                                    String name) {
}
