package ssu.eatssu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.UserInquiry;
import ssu.eatssu.domain.repository.UserInquiryRepository;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.response.BaseException;

import static ssu.eatssu.response.BaseResponseStatus.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
@Transactional
public class UserInquiryService {

    private final UserRepository userRepository;
    private final UserInquiryRepository userInquiryRepository;

    /**
     * 문의 작성
     */
    public UserInquiry createUserInquiry(Long userId, String content) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        UserInquiry userInquiry = UserInquiry.builder().user(user).content(content).build();
        return userInquiryRepository.save(userInquiry);
    }
}
