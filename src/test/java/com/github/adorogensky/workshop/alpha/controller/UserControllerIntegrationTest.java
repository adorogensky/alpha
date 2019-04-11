package com.github.adorogensky.workshop.alpha.controller;

import com.github.adorogensky.workshop.alpha.domain.dto.AddUserProfileInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.UserProfileOutputTO;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class UserControllerIntegrationTest extends BaseControllerIntegrationTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	public void getUsers() throws Exception {
		List<UserProfileOutputTO> outputUserProfileList = sendHttpRequestAndExpectStatus(
			HttpMethod.GET, "/users", HttpStatus.OK
		).andReturnObjectList(UserProfileOutputTO.class);

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
		UserProfileOutputTO addUserOutput = null;
		try {
			List<UserProfileOutputTO> outputUsers = sendHttpRequestAndExpectStatus(
				HttpMethod.GET, "/users", HttpStatus.OK
			).andReturnObjectList(UserProfileOutputTO.class);

			AddUserProfileInputTO addUserInput = new AddUserProfileInputTO();
			addUserInput.setLogin("bob");
			addUserInput.setPassword("bob");

			addUserOutput = sendHttpRequestAndExpectStatus(
				HttpMethod.POST, "/users", addUserInput, HttpStatus.OK
			).andReturnObject(UserProfileOutputTO.class);

			assertEquals(addUserInput.getLogin(), addUserOutput.getLogin());

			// verify password ?

			assertNotNull(addUserOutput.getCreated());
			assertEquals(0, LocalDateTime.now().getSecond() - addUserOutput.getCreated().getSecond());
			assertNull(addUserOutput.getModified());

			List<UserProfileOutputTO> outputUsersAfterAdd = sendHttpRequestAndExpectStatus(
				HttpMethod.GET, "/users", HttpStatus.OK
			).andReturnObjectList(UserProfileOutputTO.class);

			assertEquals(outputUsers.size() + 1, outputUsersAfterAdd.size());

			final Integer addUserOutputId = addUserOutput.getId();

			UserProfileOutputTO newAddUserOutputFromGetUsers = outputUsersAfterAdd.stream().filter(
				outputUser -> outputUser.getId() == addUserOutputId
			).findFirst().get();

			assertNotNull(newAddUserOutputFromGetUsers);
			assertEquals(addUserInput.getLogin(), newAddUserOutputFromGetUsers.getLogin());
			assertNotNull(newAddUserOutputFromGetUsers.getCreated());
			assertEquals(0, LocalDateTime.now().getSecond() - newAddUserOutputFromGetUsers.getCreated().getSecond());
			assertNull(newAddUserOutputFromGetUsers.getModified());
		} finally {
			if (addUserOutput != null) {
				entityManager.createQuery(
					"DELETE FROM UserProfile user WHERE user.id = :userId"
				).setParameter("userId", addUserOutput.getId()).executeUpdate();
			}
		}
	}
}
