package com.user.dto.request;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.user.common.Util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegistrationRequestDto {
	@NotBlank(message = "Email is mandatory")
	@Email(message = "Email is not valid", regexp = Util.VALIDATION_EMAIL_REGEXP)
	String email;

	@NotBlank(message = "Password is mandatory")
	@Size(min = 6, message = "Password should have min 6 characters")
	String password;

	@NotBlank(message = "Name is mandatory")
	String name;

	@NotBlank(message = "Mobile number is mandatory")
	@Pattern(regexp = Util.VALIDATION_MOBILE_NUMBER_REGEXP)
	String mobileNumber;

	@NotNull(message = "Date Of Birth is mandatory")
	@JsonFormat(pattern = Util.DATE_MONTH_YEAR)
	Date dateOfBirth;

	String address;
}
