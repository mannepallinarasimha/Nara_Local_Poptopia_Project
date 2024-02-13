package com.poptopia.promotion.service;

import java.util.List;

import com.poptopia.promotion.APIResponses.ApiListResponse;
import com.poptopia.promotion.entity.Region;
import com.poptopia.promotion.models.RegionRequest;

public interface RegionService {

	ApiListResponse<Region> createRegions(List<RegionRequest> regionRequestList);

	ApiListResponse<Region> getAllRegions();

}