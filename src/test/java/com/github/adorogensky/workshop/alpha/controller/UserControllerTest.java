package com.github.adorogensky.workshop.alpha.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adorogensky.workshop.alpha.domain.dto.UserProfileOutputTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void getUsers() throws Exception {
		MvcResult result = mvc.perform(
			MockMvcRequestBuilders.get("/users").contentType(MediaType.APPLICATION_JSON)
		).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		List<UserProfileOutputTO> outputUserProfileList = objectMapper.readValue(
			result.getResponse().getContentAsString(),
			new TypeReference<List<UserProfileOutputTO>>() {}
		);

		assertEquals(1, outputUserProfileList.size());
		assertEquals("alex", outputUserProfileList.get(0).getLogin());
		assertEquals(
			LocalDateTime.of(2019, 4, 2, 7, 58, 28),
			outputUserProfileList.get(0).getCreated()
		);

		assertNull(outputUserProfileList.get(0).getModified());
	}
}
