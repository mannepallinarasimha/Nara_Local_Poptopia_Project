package com.poptopia.promotion.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poptopia.promotion.APIResponses.ApiListResponse;
import com.poptopia.promotion.entity.Region;
import com.poptopia.promotion.models.RegionRequest;
import com.poptopia.promotion.repository.RegionRepository;
import com.poptopia.promotion.service.RegionService;

@Service
public class RegionServiceImpl implements RegionService {
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private RegionRepository regionRepository;
	
	@Override
	public ApiListResponse<Region> createRegions(List<RegionRequest> regionRequestList) {
		List<Region> regionsList = new ArrayList<>();
		
		for (RegionRequest regionRequest : regionRequestList) {
			Region region = new Region();
			modelMapper.map(regionRequest, region);
			System.out.println(region);
			Region savedRegion = regionRepository.save(region);
			regionsList.add(savedRegion);
		}
		return new ApiListResponse<Region>("Created Regions are...", regionsList, regionsList.size());
	}

	@Override
	public ApiListResponse<Region> getAllRegions() {
		List<Region> allRegions = regionRepository.findAll();
		return new ApiListResponse<Region>("All Regions are...", allRegions, allRegions.size());
	}



}