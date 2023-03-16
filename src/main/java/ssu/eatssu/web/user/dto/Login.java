package ssu.eatssu.web.user.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Login {

    private String email;
    private String pwd; //8자 이상 13자 이하(영숫특)
    private String nickname; //10자 제한
}
