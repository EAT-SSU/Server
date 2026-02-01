package ssu.eatssu.domain.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // OrderByIdAsc로 해줌으로써 처음 생성된 계정으로 연결된다.
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findFirstByEmailOrderByIdAsc(username)
                                  .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        return new CustomUserDetails(user);
    }

}
