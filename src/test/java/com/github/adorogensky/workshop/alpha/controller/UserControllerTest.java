package com.github.adorogensky.workshop.alpha.controller;

import com.github.adorogensky.workshop.alpha.domain.dto.AddUserProfileInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.UserProfileOutputTO;
import com.github.adorogensky.workshop.alpha.domain.entity.UserProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;
import java.time.LocalDateTime;
import java.util.List;

public class UserControllerTest extends BaseControllerIntegrationTest {

	@PersistenceContext
	private EntityManager entityManager;

	private UserProfileOutputTO addUserOutput;

	@Test
	public void getUsers() throws Exception {
		List<UserProfileOutputTO> outputUserProfileList = sendHttpRequestAndVerifyStatus(
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
		try {
			List<UserProfileOutputTO> outputUsers = sendHttpRequestAndVerifyStatus(
				HttpMethod.GET, "/users", HttpStatus.OK
			).andReturnObjectList(UserProfileOutputTO.class);

			AddUserProfileInputTO addUserInput = new AddUserProfileInputTO();
			addUserInput.setLogin("bob");
			addUserInput.setPassword("bob");

			addUserOutput = sendHttpRequestAndVerifyStatus(
				HttpMethod.POST, "/users", addUserInput, HttpStatus.OK
			).andReturnObject(UserProfileOutputTO.class);

			assertEquals(addUserInput.getLogin(), addUserOutput.getLogin());

			// verify password ?

			assertNotNull(addUserOutput.getCreated());
			assertEquals(0, LocalDateTime.now().getSecond() - addUserOutput.getCreated().getSecond());
			assertNull(addUserOutput.getModified());

			List<UserProfileOutputTO> outputUsersAfterAdd = sendHttpRequestAndVerifyStatus(
				HttpMethod.GET, "/users", HttpStatus.OK
			).andReturnObjectList(UserProfileOutputTO.class);

			assertEquals(outputUsers.size() + 1, outputUsersAfterAdd.size());

			UserProfileOutputTO newAddUserOutputFromGetUsers = outputUsersAfterAdd.stream().filter(
				outputUser -> outputUser.getId() == addUserOutput.getId()
			).findFirst().get();

			assertNotNull(newAddUserOutputFromGetUsers);
			assertEquals(addUserInput.getLogin(), newAddUserOutputFromGetUsers.getLogin());
			assertNotNull(newAddUserOutputFromGetUsers.getCreated());
			assertEquals(0, LocalDateTime.now().getSecond() - newAddUserOutputFromGetUsers.getCreated().getSecond());
			assertNull(newAddUserOutputFromGetUsers.getModified());
		} finally {
			UserProfile user = null;

			if (addUserOutput != null) {
				user = entityManager.find(UserProfile.class, addUserOutput.getId());
			}

			if (user != null) {
				entityManager.remove(user);
			}
		}
	}
}
