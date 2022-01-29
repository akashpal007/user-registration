package com.user.service;
/**
 * @author A.K.Pal.
 * 
 */
import javax.validation.Valid;

import com.user.dto.request.UserRegistrationRequestDto;
import com.user.dto.response.DefaultResponse;

public interface UserSrevice {

	DefaultResponse userRegistration(UserRegistrationRequestDto user);

}
