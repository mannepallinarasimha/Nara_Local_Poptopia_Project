package com.poptopia.promotion.APIResponses;

import lombok.Data;

@Data
public class ApiResponse<T> {
	
	private final String message;
	private final T Data;
	
}
