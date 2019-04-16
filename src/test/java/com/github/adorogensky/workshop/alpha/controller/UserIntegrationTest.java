package com.github.adorogensky.workshop.alpha.controller;

import com.github.adorogensky.workshop.alpha.domain.dto.AddUserInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.EditUserInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.ErrorTO;
import com.github.adorogensky.workshop.alpha.domain.dto.UserOutputTO;
import com.github.adorogensky.workshop.alpha.domain.entity.User;
import com.github.adorogensky.workshop.alpha.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.util.DigestUtils.md5DigestAsHex;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@SpringBootTest
@Transactional
public class UserIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void getUsers() throws Exception {
		List<UserOutputTO> outputUserProfileList = sendHttpRequestAndExpectStatus(
			HttpMethod.GET, "/users", HttpStatus.OK
		).andReturnObjectList(UserOutputTO.class);

		assertEquals(1, outputUserProfileList.size());
		assertNotNull(outputUserProfileList.get(0).getId());

		assertEquals("alex", outputUserProfileList.get(0).getLogin());
		assertEquals(
			LocalDateTime.of(2019, 4, 2, 7, 58, 28),
			outputUserProfileList.get(0).getCreated()
		);

		assertNull(outputUserProfileList.get(0).getModified());
	}

	@Test
	@Rollback(false)
	public void addUser() throws Exception {
		UserOutputTO addUserOutput = null;
		try {
			AddUserInputTO addUserInput = new AddUserInputTO();
			addUserInput.setLogin("bob");
			addUserInput.setPassword("bob");

			assertNull(userRepository.findByLogin(addUserInput.getLogin()));

			List<User> usersBeforeAdd = userRepository.findAll();

			addUserOutput = sendHttpRequestAndExpectStatus(
				HttpMethod.POST, "/users", addUserInput, HttpStatus.OK
			).andReturnObject(UserOutputTO.class);

			assertNotNull(addUserOutput.getId());
			assertEquals(addUserInput.getLogin(), addUserOutput.getLogin());
			assertNotNull(addUserOutput.getCreated());
			assertNotNull(addUserOutput.getModified());
			assertEquals(
				0,
				Duration.between(
					LocalDateTime.now(),
					addUserOutput.getCreated()
				).toMillis() / 1000
			);
			assertEquals(
				0,
				Duration.between(
					addUserOutput.getModified(),
					addUserOutput.getCreated()
				).toMillis() / 1000
			);

			List<User> usersAfterAdd = userRepository.findAll();
			assertEquals(usersBeforeAdd.size() + 1, usersAfterAdd.size());

			User addUser = userRepository.findById(addUserOutput.getId());
			assertNotNull(addUser);

			assertEquals(addUserOutput.getLogin(), addUser.getLogin());

			assertEquals(
				md5DigestAsHex(addUserInput.getPassword().getBytes()),
				addUser.getPassword()
			);

			assertEquals(addUserOutput.getCreated(), addUser.getCreated());
			assertEquals(addUserOutput.getModified(), addUser.getModified());

			usersAfterAdd.remove(addUser);
			assertEquals(usersBeforeAdd, usersAfterAdd);
		} finally {
			if (addUserOutput != null) {
				userRepository.deleteById(addUserOutput.getId());
			}
		}
	}

	@Test
	public void addUserWithEmptyLoginReturnsError() throws Exception {
		ErrorTO error = sendHttpRequestAndExpectStatus(
			HttpMethod.POST, "/users", new AddUserInputTO(), HttpStatus.BAD_REQUEST
		).andReturnObject(ErrorTO.class);

		assertEquals(
			"http://github.com/adorogensky/workshop/alpha/user/login/empty",
			error.getId()
		);

		assertEquals("User login cannot be empty", error.getMessage());
	}

	@Test
	public void addUserWithEmptyPasswordReturnsError() throws Exception {
		AddUserInputTO addUser = new AddUserInputTO();
		addUser.setLogin("john");
		addUser.setPassword("");

		ErrorTO error = sendHttpRequestAndExpectStatus(
			HttpMethod.POST, "/users", addUser, HttpStatus.BAD_REQUEST
		).andReturnObject(ErrorTO.class);

		assertEquals(
			"http://github.com/adorogensky/workshop/alpha/user/password/empty",
			error.getId()
		);

		assertEquals("User password cannot be empty", error.getMessage());
	}

	@Test
	public void addUserWithTakenLoginReturnsError() throws Exception {
		AddUserInputTO addUser = new AddUserInputTO();
		addUser.setLogin("alex");
		addUser.setPassword("alex");

		ErrorTO error = sendHttpRequestAndExpectStatus(
			HttpMethod.POST, "/users", addUser, HttpStatus.BAD_REQUEST
		).andReturnObject(ErrorTO.class);

		assertEquals(
			"http://github.com/adorogensky/workshop/alpha/user/login/taken",
			error.getId()
		);

		assertEquals("User login 'alex' cannot be used because it's taken", error.getMessage());
	}

	@Test
	public void deleteUser() throws Exception {
		User alexUser = userRepository.findByLogin("alex");
		assertNotNull(alexUser);

		sendHttpRequestAndExpectStatus(HttpMethod.DELETE, "/users/" + alexUser.getId(), HttpStatus.OK);

		assertNull(userRepository.findByLogin("alex"));
	}

	@Test
	public void deleteUserReturnsErrorWhenUserIsNotFoundById() throws Exception {
		Integer randomInt = new Random().nextInt();

		ErrorTO error = sendHttpRequestAndExpectStatus(
			HttpMethod.DELETE, "/users/" + randomInt, HttpStatus.BAD_REQUEST
		).andReturnObject(ErrorTO.class);

		assertEquals(
			"http://github.com/adorogensky/workshop/alpha/user/id/not-found",
			error.getId()
		);

		assertEquals(
			"Cannot delete user with id = '" + randomInt + "'",
			error.getMessage()
		);
	}

	@Test
	public void editUser() throws Exception {
		User alexUser = userRepository.findByLogin("alex");
		assertNotNull(alexUser);
		assertNotNull(alexUser.getCreated());
		assertNull(alexUser.getModified());

		EditUserInputTO editUser = new EditUserInputTO();
		editUser.setId(alexUser.getId());
		editUser.setLogin("alex2");
		editUser.setPassword("alex2");

		sendHttpRequestAndExpectStatus(
			HttpMethod.PUT, "/users", editUser, HttpStatus.OK
		);

		assertNull(userRepository.findByLogin("alex"));

		User editedAlexUser = userRepository.findByLogin(editUser.getLogin());

		assertEquals(editUser.getLogin(), editedAlexUser.getLogin());

		assertEquals(
			md5DigestAsHex(editUser.getPassword().getBytes()),
			editedAlexUser.getPassword()
		);

		assertEquals(alexUser.getCreated(), editedAlexUser.getCreated());

		assertNotNull(alexUser.getModified());
		assertEquals(
			0,
			Duration.between(
				LocalDateTime.now(),
				editedAlexUser.getModified()
			).toMillis() / 1000
		);
	}

	@Test
	public void editUserReturnsErrorWhenUserIsNotFoundById() throws Exception {
		EditUserInputTO editUserInput = new EditUserInputTO();
		editUserInput.setId(new Random().nextInt());

		ErrorTO error = sendHttpRequestAndExpectStatus(
			HttpMethod.PUT, "/users", editUserInput, HttpStatus.BAD_REQUEST
		).andReturnObject(ErrorTO.class);

		assertEquals("http://github.com/adorogensky/workshop/alpha/user/id/not-found", error.getId());
		assertEquals("Cannot edit user with id = '" + editUserInput.getId() + "'", error.getMessage());
	}
}
