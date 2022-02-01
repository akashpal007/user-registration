package com.user.service;

/**
 * @author A.K.Pal.
 * 
 */
import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.user.dto.request.UserLoginRequestDto;
import com.user.dto.request.UserRegistrationRequestDto;
import com.user.dto.response.DefaultResponse;
import com.user.dto.response.UserDetailsResponseDto;
import com.user.dto.response.UserLoginResponseDto;
import com.user.exception.GenericException;
import com.user.exception.TwoFactorException;
import com.user.exception.UnauthorizedUserException;
import com.user.exception.UserNotFoundException;

public interface UserSrevice {

	DefaultResponse userRegistration(UserRegistrationRequestDto user) throws JsonProcessingException, GenericException;

	DefaultResponse userVerifyOtp(String email, String otp)
			throws JsonProcessingException, GenericException, TwoFactorException;

	UserLoginResponseDto userlogin(@Valid UserLoginRequestDto userLoginRequestDto);

	UserDetailsResponseDto userDetails(Long userId, String accessToken)
			throws UnauthorizedUserException, UserNotFoundException;

	DefaultResponse userLogout(Long userId, String accessToken) throws UnauthorizedUserException, UserNotFoundException;

}
