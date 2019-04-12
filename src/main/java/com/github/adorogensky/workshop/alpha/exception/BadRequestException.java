package com.github.adorogensky.workshop.alpha.exception;

import com.github.adorogensky.workshop.alpha.domain.dto.ErrorTO;

public class BadRequestException extends RuntimeException {

	private ErrorTO error;

	public BadRequestException(ErrorTO error) {
		this.error = error;
	}

	public ErrorTO getError() {
		return error;
	}
}

