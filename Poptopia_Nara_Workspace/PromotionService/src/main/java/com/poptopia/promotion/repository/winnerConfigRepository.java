package com.poptopia.promotion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poptopia.promotion.entity.WinnerConfig;

@Repository
public interface winnerConfigRepository extends JpaRepository<WinnerConfig, Integer> {
	Optional<List<WinnerConfig>> findWinnerConfigFromDB(@Param("promoId") Integer promoId);
}
