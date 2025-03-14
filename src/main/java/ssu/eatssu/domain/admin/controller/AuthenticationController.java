package ssu.eatssu.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ssu.eatssu.domain.admin.dto.LoginRequest;
import ssu.eatssu.domain.admin.service.AuthenticationService;
import ssu.eatssu.domain.user.dto.Tokens;
import ssu.eatssu.global.handler.response.BaseResponse;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@ResponseBody
	@PostMapping("/login")
	public BaseResponse<Tokens> login(@RequestBody LoginRequest request) {
		return BaseResponse.success(authenticationService.login(request));
	}

}
