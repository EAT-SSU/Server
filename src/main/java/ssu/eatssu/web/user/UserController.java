package ssu.eatssu.web.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.UserRepository;
import ssu.eatssu.service.UserService;
import ssu.eatssu.web.user.dto.Join;
import ssu.eatssu.web.user.dto.Login;
import ssu.eatssu.web.user.dto.Tokens;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity join(@Valid @RequestBody Join join){
        userService.join(join);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 이메일 중복체크 통과 시 true 반환
     */
    @PostMapping("/user-emails/{email}/exist")
    public ResponseEntity<Boolean> checkEmailDuplicate(@PathVariable String email){
        boolean result = userRepository.existsByEmail(email);
        return ResponseEntity.ok(result);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<Tokens> login(@Valid @RequestBody Login login){
        userService.login(login.getEmail(), login.getPwd());
        return new ResponseEntity(HttpStatus.OK);
    }


}
