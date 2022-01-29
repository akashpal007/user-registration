package com.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.user.service.UserSrevice;

@RestController
public class UserController {
	@Autowired
	UserSrevice userSrevice;

}
