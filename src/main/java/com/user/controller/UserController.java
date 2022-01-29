package com.user.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.dto.request.UserRequestDto;
import com.user.dto.response.DefaultResponse;
import com.user.service.UserSrevice;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserSrevice userSrevice;

	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	@PostMapping("/registration")
	public DefaultResponse userRegistration(@Valid @RequestBody UserRequestDto user) {
		log.info("Register User UserRequestDto: " + user);

		String encPassword = bCryptPasswordEncoder.encode(user.getPassword());

		log.info("encPassword: " + encPassword);
		
		boolean check = bCryptPasswordEncoder.matches(user.getPassword(), "$2a$10$nzuU95N/A8Zq9ZpeBEqUlONC39PfgvCavTzhJkKtQ1zdPcKlDJn56");

		log.info("Check: "+ check);
		
		return new DefaultResponse("Success", "User Onborder success.");
	}

}
