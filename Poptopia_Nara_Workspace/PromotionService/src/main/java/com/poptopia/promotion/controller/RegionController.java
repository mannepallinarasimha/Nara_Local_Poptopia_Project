package com.poptopia.promotion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poptopia.promotion.APIResponses.ApiListResponse;
import com.poptopia.promotion.entity.Region;
import com.poptopia.promotion.models.RegionRequest;
import com.poptopia.promotion.service.RegionService;

@RestController
@RequestMapping(path="api/v1/region")
public class RegionController {

	@Autowired
	private RegionService regionService;
	
	@PostMapping(path="createRegions")
	public ApiListResponse<Region> createRegions(@RequestBody List<RegionRequest> regionRequestList){
		return regionService.createRegions(regionRequestList);
	}
	
	@GetMapping(path="getAllRegions")
	public ApiListResponse<Region> getAllRegions(){
		return regionService.getAllRegions();
	}
}
