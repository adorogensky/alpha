package com.github.adorogensky.workshop.alpha.domain.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class MyEntity {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public String toString(Object object) {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			return object.toString();
		}
	}
}
