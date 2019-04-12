package com.github.adorogensky.workshop.alpha.exception;

import com.github.adorogensky.workshop.alpha.domain.dto.ErrorTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BadRequestExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<ErrorTO> handle(BadRequestException exception) {
		return new ResponseEntity<>(exception.getError(), HttpStatus.BAD_REQUEST);
	}
}
