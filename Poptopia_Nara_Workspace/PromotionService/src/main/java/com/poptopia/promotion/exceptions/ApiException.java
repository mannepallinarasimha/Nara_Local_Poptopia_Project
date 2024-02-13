package com.poptopia.promotion.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
//@RequiredArgsConstructor(staticName = "of")
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final Integer errorCode;
    private final String message;
    private Map<String, Object> metaData;
//    private ApiLogger logger;
	public ApiException(HttpStatus status, Integer errorCode, String message) {
		super();
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;
	}
    

}
