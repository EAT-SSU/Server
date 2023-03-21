package ssu.eatssu.security;

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

    public static UserPrincipalDto from(CustomUserDetails userDetails){
        return UserPrincipalDto.builder()
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .build();
    }
}