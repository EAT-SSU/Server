package ssu.eatssu.global.handler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	@GetMapping("/test-manual-500"ㅎ)
	public void testManual500() {
		throw new RuntimeException("강제 500 에러 테스트");
	}
}
