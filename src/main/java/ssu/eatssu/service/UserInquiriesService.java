package ssu.eatssu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.UserInquiries;
import ssu.eatssu.domain.repository.UserInquiriesRepository;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.response.BaseException;


import static ssu.eatssu.response.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
@Transactional
public class UserInquiriesService {

    private final UserRepository userRepository;
    private final UserInquiriesRepository userInquiriesRepository;

    /**
     * 문의 작성
     */
    public UserInquiries createUserInquiries(Long userId, String content) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        UserInquiries userInquiries = UserInquiries.builder().user(user).content(content).build();
        return userInquiriesRepository.save(userInquiries);
    }
}
