package ssu.eatssu.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.handler.response.BaseResponseStatus;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        return new CustomUserDetails(user);
    }

}
