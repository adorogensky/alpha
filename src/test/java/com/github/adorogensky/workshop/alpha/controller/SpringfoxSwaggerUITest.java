package com.github.adorogensky.workshop.alpha.controller;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

public class SpringfoxSwaggerUITest extends BaseControllerIntegrationTest {

	@Test
	public void rootPathDisplaysSwaggerUIPage() throws Exception {
		sendHttpRequestAndExpectStatus(HttpMethod.GET, "/", HttpStatus.FOUND).andExpectRedirectedUrl("/swagger-ui.html");
	}

	@Test
	public void swaggerUIPageDisplays() throws Exception {
		sendHttpRequestAndExpectStatus(HttpMethod.GET, "/swagger-ui.html", HttpStatus.OK).andExpectResponseContains("<div id=\"swagger-ui\"></div>");
	}
}
