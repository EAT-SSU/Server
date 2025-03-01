package ssu.eatssu.domain.auth.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipalDto {
    private Long id;
    private String email;
    private String role;

    public static UserPrincipalDto from(CustomUserDetails userDetails) {
        return UserPrincipalDto.builder()
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .role(userDetails.getRole().getAuthority())
                .build();
    }
}