package com.poptopia.promotion.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCreateRequest {
	private Integer regionId;
	private Integer epsilonId;
	private String moduleKey;
	private String promotionName;
	private String localTimeZone;
}
