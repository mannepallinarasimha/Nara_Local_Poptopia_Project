package com.poptopia.promotion.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequest {

	private Integer clusterId;
	private List<PromotionCreateRequest> promotions;
	private MechanicRequest mechanic;
	private List<SettingRequest> settings;
}