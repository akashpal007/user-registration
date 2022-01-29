package com.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.common.Status;
import com.user.dto.request.UserRegistrationRequestDto;
import com.user.dto.response.DefaultResponse;
import com.user.entity.UserEntity;
import com.user.repository.UserRepository;
import com.user.service.UserSrevice;

@Service
public class UserServiceImpl implements UserSrevice {
	@Autowired
	UserRepository userRepo;

	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	@Override
	public DefaultResponse userRegistration(UserRegistrationRequestDto userRegReq) {
		/* UserRequestDto MAP TO UserEntity */
		UserEntity userEntity = mapUserRequestDtoToUserEntity(userRegReq);
		/* Save data in User db */
		userRepo.save(userEntity);
		/* Send OTP */

		return new DefaultResponse(Status.SUCCESS, "User onboard successfully. Please verify the user.");
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

}
