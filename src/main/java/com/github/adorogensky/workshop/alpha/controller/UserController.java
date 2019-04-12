package com.github.adorogensky.workshop.alpha.controller;

import com.github.adorogensky.workshop.alpha.domain.dto.AddUserInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.UserOutputTO;
import com.github.adorogensky.workshop.alpha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public List<UserOutputTO> getUsers() {
		return userService.getUsers();
	}

	@PostMapping("/users")
	public UserOutputTO addUser(@RequestBody AddUserInputTO addUserInput) {
		return userService.addUser(addUserInput);
	}

	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable("id") Integer id) {
		userService.deleteUser(id);
	}
}
