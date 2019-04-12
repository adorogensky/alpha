package com.github.adorogensky.workshop.alpha.domain.dto;

public class ErrorTO {

	private String id;

	private String message;

	// Required so ObjectMapper.readValue() can create an instance of this class
	private ErrorTO() {}

	public ErrorTO(String id, String message) {
		this.id = id;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
