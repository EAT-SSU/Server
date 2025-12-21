package ssu.eatssu.domain.auth.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.user.entity.DeviceType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipalDto {
    private Long id;
    private String email;
    private DeviceType deviceType;
    private String role;

    public static UserPrincipalDto from(CustomUserDetails userDetails) {
        return UserPrincipalDto.builder()
                               .id(userDetails.getId())
                               .email(userDetails.getEmail())
                               .deviceType(userDetails.getDeviceType())
                               .role(userDetails.getRole().getAuthority())
                               .build();
    }
}
