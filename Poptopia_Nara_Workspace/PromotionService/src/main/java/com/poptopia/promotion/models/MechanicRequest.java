package com.poptopia.promotion.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MechanicRequest {

	private String type;
	private String startDate;
	private String endDate;
	private String attributeCode;
	private String attributeValue;
}
