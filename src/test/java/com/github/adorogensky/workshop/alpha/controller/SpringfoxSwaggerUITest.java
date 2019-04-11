package com.github.adorogensky.workshop.alpha.controller;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@WebMvcTest
public class SpringfoxSwaggerUITest extends BaseControllerIntegrationTest {

	@MockBean
	private UserController userController;

	@Test
	public void rootPathDisplaysSwaggerUIPage() throws Exception {
		sendHttpRequestAndExpectStatus(HttpMethod.GET, "/", HttpStatus.FOUND).andExpectRedirectedUrl("/swagger-ui.html");
	}

	@Test
	public void swaggerUIPageDisplays() throws Exception {
		sendHttpRequestAndExpectStatus(HttpMethod.GET, "/swagger-ui.html", HttpStatus.OK).andExpectResponseContains("<div id=\"swagger-ui\"></div>");
	}
}
