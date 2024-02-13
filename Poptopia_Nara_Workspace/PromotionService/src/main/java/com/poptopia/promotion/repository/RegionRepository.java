package com.poptopia.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poptopia.promotion.entity.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {

}
