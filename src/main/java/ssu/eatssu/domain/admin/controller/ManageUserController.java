package ssu.eatssu.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.util.RandomNicknameUtil;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class ManageUserController {

    private final UserRepository userRepository;
    private final RandomNicknameUtil randomNicknameUtil;

    @ResponseBody
    @PostMapping("/update-default-nicknames")
    @Transactional
    public BaseResponse<Integer> updateDefaultNicknames() {
        List<User> users = userRepository.findByNicknameLikeUserPattern();
        
        int updatedCount = 0;
        for (User user : users) {
            String newNickname = randomNicknameUtil.generate();
            user.updateNickname(newNickname);
            updatedCount++;
        }
        
        userRepository.saveAll(users);
        
        return BaseResponse.success(updatedCount);
    }
}

