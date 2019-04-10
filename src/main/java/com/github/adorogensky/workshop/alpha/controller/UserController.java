package com.github.adorogensky.workshop.alpha.controller;

import com.github.adorogensky.workshop.alpha.domain.dto.AddUserProfileInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.UserProfileOutputTO;
import com.github.adorogensky.workshop.alpha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public List<UserProfileOutputTO> getUsers() {
		return userService.getUsers();
	}

	@PostMapping("/users")
	public UserProfileOutputTO addUser(@RequestBody AddUserProfileInputTO addUserInput) {
		return userService.addUser(addUserInput);
	}
}
