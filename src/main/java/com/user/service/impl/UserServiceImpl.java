package com.user.service.impl;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.user.common.Status;
import com.user.common.UserStatus;
import com.user.common.Util;
import com.user.dto.request.UserLoginRequestDto;
import com.user.dto.request.UserRegistrationRequestDto;
import com.user.dto.response.DefaultResponse;
import com.user.dto.response.TwoFactorResponse;
import com.user.dto.response.UserDetailsResponseDto;
import com.user.dto.response.UserLoginResponseDto;
import com.user.entity.UserEntity;
import com.user.exception.GenericException;
import com.user.exception.TwoFactorException;
import com.user.exception.UnauthorizedUserException;
import com.user.exception.UserNotFoundException;
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
	public DefaultResponse userRegistration(UserRegistrationRequestDto userRegReq)
			throws JsonProcessingException, GenericException {
		/* Verify data AND Save UserRegistrationRequestDto in database */
		DefaultResponse verifiedResponse = saveUserRegistrationData(userRegReq);

		if (Status.SUCCESS.equals(verifiedResponse.getStatus())) {
			if ("User saved in DB.".equals(verifiedResponse.getDetails())) {
				/* Send OTP */
				TwoFactorResponse twoFactorResponse = twoFactor.sendingSmsOtp(userRegReq.getMobileNumber());
				log.info("Send OTP Response: " + twoFactorResponse);

				return new DefaultResponse(Status.SUCCESS, "User onboard successfully. Please verify the user.");
			} else {
				return verifiedResponse;
			}
		}
		return verifiedResponse;
	}

	@Transactional
	private DefaultResponse saveUserRegistrationData(UserRegistrationRequestDto userRegReq) {
		UserEntity userEntityResponse = userRepo
				.findByEmailOrMobile(userRegReq.getEmail(), userRegReq.getMobileNumber()).orElse(null);
		if (userEntityResponse == null) {
			/* UserRequestDto MAP TO UserEntity */
			UserEntity userEntity = mapUserRequestDtoToUserEntity(userRegReq);
			/* Save data in User db */
			userRepo.save(userEntity);
			return new DefaultResponse(Status.SUCCESS, "User saved in DB.");
		} else {
			if (userEntityResponse.getEmail().equals(userRegReq.getEmail())
					&& userEntityResponse.getMobile().equals(userRegReq.getMobileNumber())) {
				if (userEntityResponse.getVerified()) {
					return new DefaultResponse(Status.SUCCESS,
							"User onboarding and verification successfully done. Please Login.");
				} else {
					return new DefaultResponse(Status.SUCCESS, "User onboard successfully. Please verify the user.");
				}
			} else if (userEntityResponse.getEmail().equals(userRegReq.getEmail())) {
				return new DefaultResponse(Status.FAILED,
						"User with this email is already exists. Email: " + userRegReq.getEmail());
			} else {
				return new DefaultResponse(Status.FAILED,
						"User with this Mobile Number is already exists. Mobile Number: "
								+ userRegReq.getMobileNumber());
			}
		}
	}

	@Override
	public DefaultResponse userVerifyOtp(String email, String otp)
			throws JsonProcessingException, GenericException, TwoFactorException {
		/* Get user data from db */
		UserEntity userEntity = userRepo.findByEmail(email).orElse(null);
		if (userEntity == null) {
			return new DefaultResponse(Status.FAILED, "User is not registred. Please register.");
		}

		if (Boolean.TRUE.equals(userEntity.getVerified())) {
			return new DefaultResponse(Status.SUCCESS, "User already verification successfully. Please login.");
		}

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
					return new UserLoginResponseDto(userEntity.getId(), UserStatus.VERIFIED_USER.toString(), newToken,
							"New access token generated successfully");
				} else {
					return new UserLoginResponseDto(null, UserStatus.VERIFIED_USER.toString(), null,
							"Password mismatch. Please enter your currect password.");
				}
			} else {
				return new UserLoginResponseDto(null, UserStatus.VERIFICATION_PENDING.toString(), null,
						"OTP authentication is pending.");
			}
		} else {
			return new UserLoginResponseDto(null, UserStatus.UNKNOWN_USER.toString(), null,
					"The user is not registered with the system.");
		}

	}

	@Override
	public UserDetailsResponseDto userDetails(Long userId, String accessToken)
			throws UnauthorizedUserException, UserNotFoundException {
		/* Get user data from db */
		UserEntity userEntity = userRepo.findById(userId).orElse(null);

		if (userEntity != null) {
			if (Boolean.TRUE.equals(userEntity.getVerified())) {
				if (userEntity.getAccessToken().equals(accessToken)) {
					/* UserEntity MAP TO UserDetailsResponseDto */
					return mapUserEntityToUserDetailsResponseDto(userEntity);

				} else {
					throw new UnauthorizedUserException(UserStatus.UNAUTHORISED_USER + " " + "Invalide access token");
				}

			} else {
				throw new UnauthorizedUserException(
						UserStatus.VERIFICATION_PENDING.toString() + " " + "OTP authentication is pending.");
			}
		} else {
			throw new UserNotFoundException(
					UserStatus.UNKNOWN_USER.toString() + " " + "The user is not registered with the system.");
		}
	}

	@Override
	public DefaultResponse userLogout(Long userId, String accessToken)
			throws UnauthorizedUserException, UserNotFoundException {
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
					throw new UnauthorizedUserException(UserStatus.UNAUTHORISED_USER + " " + "Invalide access token");
				}

			} else {
				throw new UnauthorizedUserException(
						UserStatus.VERIFICATION_PENDING.toString() + " " + "OTP authentication is pending.");
			}
		} else {
			throw new UserNotFoundException(
					UserStatus.UNKNOWN_USER.toString() + " " + "The user is not registered with the system.");
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
