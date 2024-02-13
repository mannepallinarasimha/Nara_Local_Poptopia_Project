package com.poptopia.promotion.APIResponses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiListResponse<T> {
	
	private final String message;
	private final List<T> data;
	private final Integer length;

}
