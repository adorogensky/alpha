package com.github.adorogensky.workshop.alpha.controller;

import com.github.adorogensky.workshop.alpha.domain.dto.AddUserInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.ErrorTO;
import com.github.adorogensky.workshop.alpha.domain.dto.UserOutputTO;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class UserIntegrationTest extends AbstractIntegrationTest {

	@PersistenceContext
	private EntityManager entityManager;

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
	@Transactional
	@Rollback(false)
	public void addUser() throws Exception {
		UserOutputTO addUserOutput = null;
		try {
			List<UserOutputTO> outputUsers = sendHttpRequestAndExpectStatus(
				HttpMethod.GET, "/users", HttpStatus.OK
			).andReturnObjectList(UserOutputTO.class);

			AddUserInputTO addUserInput = new AddUserInputTO();
			addUserInput.setLogin("bob");
			addUserInput.setPassword("bob");

			addUserOutput = sendHttpRequestAndExpectStatus(
				HttpMethod.POST, "/users", addUserInput, HttpStatus.OK
			).andReturnObject(UserOutputTO.class);

			assertEquals(addUserInput.getLogin(), addUserOutput.getLogin());

			// verify password ?

			assertNotNull(addUserOutput.getCreated());
			assertEquals(
				0,
				Duration.between(
					LocalDateTime.now(),
					addUserOutput.getCreated()
				).toMillis() / 1000
			);
			assertNull(addUserOutput.getModified());

			List<UserOutputTO> outputUsersAfterAdd = sendHttpRequestAndExpectStatus(
				HttpMethod.GET, "/users", HttpStatus.OK
			).andReturnObjectList(UserOutputTO.class);

			assertEquals(outputUsers.size() + 1, outputUsersAfterAdd.size());

			final int addUserOutputId = addUserOutput.getId();

			UserOutputTO newAddUserOutputFromGetUsers = outputUsersAfterAdd.stream().filter(
				outputUser -> outputUser.getId() == addUserOutputId
			).findFirst().get();

			assertNotNull(newAddUserOutputFromGetUsers);
			assertEquals(addUserInput.getLogin(), newAddUserOutputFromGetUsers.getLogin());
			assertNotNull(newAddUserOutputFromGetUsers.getCreated());
			assertEquals(
				0,
				Duration.between(
					LocalDateTime.now(), newAddUserOutputFromGetUsers.getCreated()
				).toMillis() / 1000
			);
			assertNull(newAddUserOutputFromGetUsers.getModified());
		} finally {
			if (addUserOutput != null) {
				entityManager.createQuery(
					"DELETE FROM User user WHERE user.id = :userId"
				).setParameter("userId", addUserOutput.getId()).executeUpdate();
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
	@Transactional
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
}
