package com.poptopia.promotion.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.poptopia.promotion.entity.Promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionClusterRequest {

	@JsonProperty(value = "clusterName")	
	private String clusterName;

	@JsonProperty(value = "promotions")
	@JsonInclude(Include.NON_NULL)
	private List<Promotion> promotions;
}