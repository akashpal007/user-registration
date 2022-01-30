package com.user.service;
/**
 * @author A.K.Pal.
 * 
 */
import javax.validation.Valid;

import com.user.dto.request.UserLoginRequestDto;
import com.user.dto.request.UserRegistrationRequestDto;
import com.user.dto.response.DefaultResponse;
import com.user.dto.response.UserDetailsResponseDto;
import com.user.dto.response.UserLoginResponseDto;

public interface UserSrevice {

	DefaultResponse userRegistration(UserRegistrationRequestDto user);

	DefaultResponse userVerifyOtp(String email, String otp);

	UserLoginResponseDto userlogin(@Valid UserLoginRequestDto userLoginRequestDto);

	UserDetailsResponseDto userDetails(Long userId, String accessToken) throws Exception;

	DefaultResponse userLogout(Long userId, String accessToken) throws Exception;

}
