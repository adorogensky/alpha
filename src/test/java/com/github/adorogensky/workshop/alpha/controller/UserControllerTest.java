package com.github.adorogensky.workshop.alpha.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adorogensky.workshop.alpha.domain.dto.AddUserProfileInputTO;
import com.github.adorogensky.workshop.alpha.domain.dto.UserProfileOutputTO;
import com.github.adorogensky.workshop.alpha.domain.entity.UserProfile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@PersistenceContext
	private EntityManager entityManager;

	private UserProfileOutputTO addUserOutput;

	@Test
	public void getUsers() throws Exception {
		MvcResult result = mvc.perform(
			get("/users").contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk()).andReturn();

		List<UserProfileOutputTO> outputUserProfileList = objectMapper.readValue(
			result.getResponse().getContentAsString(),
			new TypeReference<List<UserProfileOutputTO>>() {}
		);

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
			MvcResult getUsersResult = mvc.perform(
				get("/users").contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk()).andReturn();

			List<UserProfileOutputTO> outputUsers = objectMapper.readValue(
				getUsersResult.getResponse().getContentAsString(),
				new TypeReference<List<UserProfileOutputTO>>() {}
			);

			AddUserProfileInputTO addUserInput = new AddUserProfileInputTO();
			addUserInput.setLogin("bob");
			addUserInput.setPassword("bob");

			MvcResult addUserResult = mvc.perform(
				post("/users").contentType(MediaType.APPLICATION_JSON).content(
					objectMapper.writeValueAsString(addUserInput)
				)
			).andExpect(status().isOk()).andReturn();

			addUserOutput = objectMapper.readValue(
				addUserResult.getResponse().getContentAsString(),
				UserProfileOutputTO.class
			);

			assertEquals(addUserInput.getLogin(), addUserOutput.getLogin());

			// verify password ?

			assertNotNull(addUserOutput.getCreated());
			assertEquals(0, LocalDateTime.now().getSecond() - addUserOutput.getCreated().getSecond());
			assertNull(addUserOutput.getModified());

			MvcResult getUsersAfterAddResult = mvc.perform(
				get("/users").contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk()).andReturn();

			List<UserProfileOutputTO> outputUsersAfterAdd = objectMapper.readValue(
				getUsersAfterAddResult.getResponse().getContentAsString(),
				new TypeReference<List<UserProfileOutputTO>>() {}
			);

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
