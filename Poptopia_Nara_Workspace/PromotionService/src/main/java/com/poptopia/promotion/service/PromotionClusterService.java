package com.poptopia.promotion.service;

import com.poptopia.promotion.APIResponses.ApiResponse;
import com.poptopia.promotion.entity.PromotionCluster;
import com.poptopia.promotion.models.PromotionClusterRequest;

public interface PromotionClusterService {

	ApiResponse<PromotionCluster> createPromotionCluster(PromotionClusterRequest promotionClusterRequest);

}
