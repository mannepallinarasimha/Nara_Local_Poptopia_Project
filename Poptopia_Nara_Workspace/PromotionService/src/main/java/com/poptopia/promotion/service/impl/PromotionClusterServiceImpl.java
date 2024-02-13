package com.poptopia.promotion.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poptopia.promotion.APIResponses.ApiResponse;
import com.poptopia.promotion.entity.PromotionCluster;
import com.poptopia.promotion.models.PromotionClusterRequest;
import com.poptopia.promotion.repository.PromotionClusterRepository;
import com.poptopia.promotion.service.PromotionClusterService;

@Service
public class PromotionClusterServiceImpl implements PromotionClusterService {
	
	@Autowired
	private PromotionClusterRepository promotionClusterRepository;

	@Override
	public ApiResponse<PromotionCluster> createPromotionCluster(PromotionClusterRequest promotionClusterRequest) {
		PromotionCluster promotionCluster = new PromotionCluster();
		promotionCluster.setName(promotionClusterRequest.getClusterName());
		promotionClusterRepository.save(promotionCluster);
		return new ApiResponse<PromotionCluster>("The Cluster Details are...", promotionCluster);
	}

}
