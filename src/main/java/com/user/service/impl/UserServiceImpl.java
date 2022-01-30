package com.user.service.impl;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.common.Status;
import com.user.common.Util;
import com.user.dto.request.UserLoginRequestDto;
import com.user.dto.request.UserRegistrationRequestDto;
import com.user.dto.response.DefaultResponse;
import com.user.dto.response.TwoFactorResponse;
import com.user.dto.response.UserDetailsResponseDto;
import com.user.dto.response.UserLoginResponseDto;
import com.user.entity.UserEntity;
import com.user.integration.TwoFactor;
import com.user.repository.UserRepository;
import com.user.service.UserSrevice;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserSrevice {
	@Autowired
	UserRepository userRepo;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	TwoFactor twoFactor;

	@Override
	public DefaultResponse userRegistration(UserRegistrationRequestDto userRegReq) {
		/* UserRequestDto MAP TO UserEntity */
		UserEntity userEntity = mapUserRequestDtoToUserEntity(userRegReq);
		/* Save data in User db */
		userRepo.save(userEntity);
		/* Send OTP */
		TwoFactorResponse twoFactorResponse = twoFactor.sendingSmsOtp(userEntity.getMobile());
		log.info("Send OTP Response: " + twoFactorResponse);

		return new DefaultResponse(Status.SUCCESS, "User onboard successfully. Please verify the user.");
	}

	@Override
	public DefaultResponse userVerifyOtp(String email, String otp) {
		/* Get user data from db */
		UserEntity userEntity = userRepo.findByEmail(email).orElse(null);

		/* Verify OTP */
		TwoFactorResponse twoFactorResponse = twoFactor.verifySmsOtp(userEntity, otp);
		log.info("Send OTP Response: " + twoFactorResponse);

		return new DefaultResponse(Status.SUCCESS, "User verification successfully. Please login.");
	}

	@Override
	public UserLoginResponseDto userlogin(@Valid UserLoginRequestDto userLoginRequestDto) {
		/* Get user data from db */
		UserEntity userEntity = userRepo.findByEmail(userLoginRequestDto.getEmail()).orElse(null);

		if (userEntity != null) {
			if (Boolean.TRUE.equals(userEntity.getVerified())) {
				/* Verify password */
				boolean passwordVerification = bCryptPasswordEncoder.matches(userLoginRequestDto.getPassword(),
						userEntity.getPassword());

				if (Boolean.TRUE.equals(passwordVerification)) {
					/* Creating new access token and save in db */
					String newToken = Util.newToken();
					userEntity.setAccessToken(newToken);
					userRepo.save(userEntity);

					/* Success response with new access token */
					return new UserLoginResponseDto("Verified User", newToken,
							"New access token generated successfully");
				} else {
					/* Success response with new access token */
					return new UserLoginResponseDto("Verified User", null,
							"Password mismatch. Please enter your currect password.");
				}
			} else {
				return new UserLoginResponseDto("Verification Pendingr", null, "OTP authentication is pending.");
			}
		} else {
			return new UserLoginResponseDto("Unknown User", null, "The user is not registered with the system.");
		}

	}

	@Override
	public UserDetailsResponseDto userDetails(Long userId, String accessToken) throws Exception {
		/* Get user data from db */
		UserEntity userEntity = userRepo.findById(userId).orElse(null);

		if (userEntity != null) {
			if (Boolean.TRUE.equals(userEntity.getVerified())) {
				if (userEntity.getAccessToken().equals(accessToken)) {
					/* UserEntity MAP TO UserDetailsResponseDto */
					return mapUserEntityToUserDetailsResponseDto(userEntity);

				} else {
					throw new Exception("Unauthorised user. Invalide access token");
				}

			} else {
				throw new Exception("Verification Pending. OTP authentication is pending.");
			}
		} else {
			throw new Exception("Unknown User. The user is not registered with the system.");
		}
	}

	@Override
	public DefaultResponse userLogout(Long userId, String accessToken) throws Exception {
		/* Get user data from db */
		UserEntity userEntity = userRepo.findById(userId).orElse(null);

		if (userEntity != null) {
			if (Boolean.TRUE.equals(userEntity.getVerified())) {
				if (userEntity.getAccessToken().equals(accessToken)) {
					/* UserEntity MAP TO UserDetailsResponseDto */
					String newToken = Util.newToken();
					userEntity.setAccessToken(newToken);
					userRepo.save(userEntity);

					return new DefaultResponse(Status.SUCCESS, "User logout successfullyr.");

				} else {
					throw new Exception("Unauthorised user. Invalide access token");
				}

			} else {
				throw new Exception("Verification Pending. OTP authentication is pending.");
			}
		} else {
			throw new Exception("Unknown User. The user is not registered with the system.");
		}
	}
	
	/**
	 * @param UserRegistrationRequestDto
	 * @return UserEntity
	 */
	private UserEntity mapUserRequestDtoToUserEntity(UserRegistrationRequestDto userRegReq) {
		UserEntity userEntity = new UserEntity();
		userEntity.setEmail(userRegReq.getEmail());
		userEntity.setName(userRegReq.getName());
		userEntity.setMobile(userRegReq.getMobileNumber());
		userEntity.setDateOfBirth(userRegReq.getDateOfBirth());
		userEntity.setAddress(userRegReq.getAddress());
		/* Encoder Password */
		String password = bCryptPasswordEncoder.encode(userRegReq.getPassword());
		userEntity.setPassword(password);
		return userEntity;
	}

	/**
	 * @param UserEntity
	 * @return UserDetailsResponseDto
	 */
	private UserDetailsResponseDto mapUserEntityToUserDetailsResponseDto(UserEntity userEntity) {
		UserDetailsResponseDto userDetailsResponseDto = new UserDetailsResponseDto();
		userDetailsResponseDto.setEmail(userEntity.getEmail());
		userDetailsResponseDto.setName(userEntity.getName());
		userDetailsResponseDto.setMobileNumber(userEntity.getMobile());
		userDetailsResponseDto.setDateOfBirth(userEntity.getDateOfBirth());
		userDetailsResponseDto.setAddress(userEntity.getAddress());

		return userDetailsResponseDto;
	}

}
