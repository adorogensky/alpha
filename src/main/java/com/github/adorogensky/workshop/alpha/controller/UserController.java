package com.github.adorogensky.workshop.alpha.controller;

import com.github.adorogensky.workshop.alpha.domain.dto.AddUserInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.EditUserInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.UserOutputTO;
import com.github.adorogensky.workshop.alpha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping
	public List<UserOutputTO> getUsers() {
		return userService.getUsers();
	}

	@PostMapping
	public UserOutputTO addUser(@RequestBody AddUserInputTO addUserInput) {
		return userService.addUser(addUserInput);
	}

	@DeleteMapping("{id}")
	public void deleteUser(@PathVariable("id") Integer id) {
		userService.deleteUser(id);
	}

	@PutMapping
	public void editUser(@RequestBody EditUserInputTO editUserInput) {
		userService.editUser(editUserInput);
	}
}
