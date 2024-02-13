package com.poptopia.promotion.service;

import com.poptopia.promotion.APIResponses.ApiListResponse;
import com.poptopia.promotion.models.PromotionRequest;
import com.poptopia.promotion.models.PromotionResponse;

public interface PromotionService {

	ApiListResponse<PromotionResponse> createPromotion(PromotionRequest promotionRequest);

}
