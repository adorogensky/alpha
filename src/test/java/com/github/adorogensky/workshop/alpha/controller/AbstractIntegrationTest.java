package com.github.adorogensky.workshop.alpha.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

	class SendHttpRequestResult {

		private MvcResult mvcResult;

		private SendHttpRequestResult(MvcResult mvcResult) {
			this.mvcResult = mvcResult;
		}

		protected  <T> List<T> andReturnObjectList(Class<T> clazz) throws Exception {
			return objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				objectMapper.getTypeFactory().constructParametricType(ArrayList.class, clazz)
			);
		}

		protected  <T> T andReturnObject(Class<T> clazz) throws Exception {
			return objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				clazz
			);
		}

		protected SendHttpRequestResult andExpectRedirectedUrl(String redirectedUrl) {
			assertEquals(redirectedUrl, getField(mvcResult.getModelAndView().getView(), "url"));
			return new SendHttpRequestResult(mvcResult);
		}

		protected SendHttpRequestResult andExpectResponseContains(String text) throws Exception {
			assertTrue(mvcResult.getResponse().getContentAsString().contains(text));
			return new SendHttpRequestResult(mvcResult);
		}
	}

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;


	protected SendHttpRequestResult sendHttpRequestAndExpectStatus(HttpMethod httpMethod, String uriPath, HttpStatus expectedHttpStatus) throws Exception {
		return sendHttpRequestAndExpectStatus(httpMethod, uriPath, null, expectedHttpStatus);
	}

	protected SendHttpRequestResult sendHttpRequestAndExpectStatus(HttpMethod httpMethod, String uriPath, Object content, HttpStatus expectedHttpStatus) throws Exception {
		MockHttpServletRequestBuilder requestBuilder = request(httpMethod, uriPath).contentType(MediaType.APPLICATION_JSON);

		if (content != null) {
			requestBuilder.content(objectMapper.writeValueAsString(content));
		}

		MvcResult mvcResult = mvc.perform(requestBuilder).andExpect(
			result ->
				assertEquals(
					"Status expected:<" + expectedHttpStatus.value() + "> but was:<" + result.getResponse().getStatus() + ">",
					expectedHttpStatus.value(), result.getResponse().getStatus()
				)
		).andReturn();

		return new SendHttpRequestResult(mvcResult);
	}
}
