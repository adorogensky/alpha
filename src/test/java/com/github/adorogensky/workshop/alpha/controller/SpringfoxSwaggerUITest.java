package com.github.adorogensky.workshop.alpha.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringfoxSwaggerUITest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void rootPathDisplaysSwaggerUIPage() throws Exception {
		MvcResult result = mvc.perform(
			get("/").contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is3xxRedirection()).andReturn();

		assertEquals("/swagger-ui.html", getField(result.getModelAndView().getView(), "url"));
	}

	@Test
	public void swaggerUIPageDisplays() throws Exception {
		MvcResult result = mvc.perform(
			get("/swagger-ui.html").contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk()).andReturn();

		String htmlResponse = result.getResponse().getContentAsString();
		assertTrue(htmlResponse.contains("<div id=\"swagger-ui\"></div>"));
	}
}
