package com.poptopia.promotion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poptopia.promotion.entity.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

	Optional<Promotion> findByEpsilonId(Integer promotionEpsilonId);

	Optional<Promotion> findByModuleKey(String promotionModuleKey);

	Optional<Promotion> findByName(String promotionName);

}