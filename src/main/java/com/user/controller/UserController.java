package com.user.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user.common.Util;
import com.user.dto.request.UserLoginRequestDto;
import com.user.dto.request.UserRegistrationRequestDto;
import com.user.dto.response.DefaultResponse;
import com.user.service.UserSrevice;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserSrevice userSrevice;
	
	@Autowired
	Util util;

	@PostMapping("/registration")
	public DefaultResponse userRegistration(@Valid @RequestBody UserRegistrationRequestDto userRegReq) {
		log.info("User Registration UserRequestDto: " + userRegReq);
		
		return userSrevice.userRegistration(userRegReq);
	}
	
	@PostMapping("/verify-otp")
	public DefaultResponse userVerifyOtp(@RequestParam String userId, @RequestParam String otp) {
		log.info("User Verify Otp userId: " + userId + "|| otp: "+ otp);
		
		return new DefaultResponse();
	}

	@PostMapping("/login")
	public DefaultResponse userlogin(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto) {
		log.info("User login UserLoginRequestDto: " + userLoginRequestDto);
		
		return new DefaultResponse();
	}
	
	@GetMapping("/details")
	public DefaultResponse userDetails(@RequestParam String userId) {
		log.info("User Details userId: " + userId);
		log.info("User Details HeaderToken: " + util.getHeaderToken());
		
		return new DefaultResponse();
	}
	
	@PostMapping("/logout")
	public DefaultResponse userLogout(@RequestParam String userId) {
		log.info("User logout userId: " + userId);
		log.info("User logout HeaderToken: " + util.getHeaderToken());
		
		return new DefaultResponse();
	}

}
