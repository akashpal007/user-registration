package com.user.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.user.common.Util;
import com.user.dto.request.UserLoginRequestDto;
import com.user.dto.request.UserRegistrationRequestDto;
import com.user.dto.response.DefaultResponse;
import com.user.dto.response.UserDetailsResponseDto;
import com.user.dto.response.UserLoginResponseDto;
import com.user.exception.GenericException;
import com.user.exception.TwoFactorException;
import com.user.exception.UnauthorizedUserException;
import com.user.exception.UserNotFoundException;
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
	public DefaultResponse userRegistration(@Valid @RequestBody UserRegistrationRequestDto userRegReq)
			throws JsonProcessingException, GenericException {
		log.info("User Registration UserRequestDto: " + userRegReq);

		return userSrevice.userRegistration(userRegReq);
	}

	@PostMapping("/verify-otp")
	public DefaultResponse userVerifyOtp(@RequestParam String email, @RequestParam String otp)
			throws JsonProcessingException, GenericException, TwoFactorException {
		log.info("User Verify Otp email: " + email + "|| otp: " + otp);

		return userSrevice.userVerifyOtp(email, otp);
	}

	@PostMapping("/login")
	public UserLoginResponseDto userlogin(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto) {
		log.info("User login UserLoginRequestDto: " + userLoginRequestDto);

		return userSrevice.userlogin(userLoginRequestDto);
	}

	@GetMapping("/details")
	public UserDetailsResponseDto userDetails(@RequestParam Long userId)
			throws UnauthorizedUserException, UserNotFoundException {
		log.info("User Details userId: " + userId);

		return userSrevice.userDetails(userId, util.getHeaderToken());
	}

	@PostMapping("/logout")
	public DefaultResponse userLogout(@RequestParam Long userId)
			throws UnauthorizedUserException, UserNotFoundException {
		log.info("User logout userId: " + userId);
		log.info("User logout HeaderToken: " + util.getHeaderToken());

		return userSrevice.userLogout(userId, util.getHeaderToken());
	}

}
