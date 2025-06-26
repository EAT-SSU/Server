package ssu.eatssu.global.handler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorTestController {
    @GetMapping("/error-test")
    public String triggerError() {
        throw new RuntimeException("임의로 발생시킨 런타임 에러입니다!");
    }
}
