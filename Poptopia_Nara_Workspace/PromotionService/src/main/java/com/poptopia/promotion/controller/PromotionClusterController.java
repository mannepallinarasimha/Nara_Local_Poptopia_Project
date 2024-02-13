package com.poptopia.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poptopia.promotion.APIResponses.ApiResponse;
import com.poptopia.promotion.entity.PromotionCluster;
import com.poptopia.promotion.models.PromotionClusterRequest;
import com.poptopia.promotion.service.PromotionClusterService;

@RestController
@RequestMapping(path="api/v1/promotioncluster")
public class PromotionClusterController {
	@Autowired
	private PromotionClusterService promotionClusterService;
	
	@PostMapping(path="createCluster")
	public ApiResponse<PromotionCluster> createPromotionCluster(@RequestBody PromotionClusterRequest promotionClusterRequest){
		return promotionClusterService.createPromotionCluster(promotionClusterRequest);
	}

}
