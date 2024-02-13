package com.poptopia.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poptopia.promotion.APIResponses.ApiListResponse;
import com.poptopia.promotion.models.PromotionRequest;
import com.poptopia.promotion.models.PromotionResponse;
import com.poptopia.promotion.service.PromotionService;

@RestController
@RequestMapping(path="api/v1/promotions")
public class PromotionController {
	
	@Autowired
	private PromotionService promotionService;

	@PostMapping(path="createPromotion")
	public ApiListResponse<PromotionResponse> createPromotion(@RequestBody PromotionRequest promotionRequest){
		return promotionService.createPromotion(promotionRequest);
	}
}
