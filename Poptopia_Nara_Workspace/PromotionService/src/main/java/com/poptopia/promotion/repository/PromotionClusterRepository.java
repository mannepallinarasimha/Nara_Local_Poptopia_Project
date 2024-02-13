package com.poptopia.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poptopia.promotion.entity.PromotionCluster;

@Repository
public interface PromotionClusterRepository extends JpaRepository<PromotionCluster, Integer> {

}
