package ssu.eatssu.domain.user.dto;

import lombok.Builder;

@Builder
public record GetDepartmentResponse(Long id,
                                    String name) {
}
