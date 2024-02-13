package com.poptopia.promotion.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="promotion_master")
public class Promotion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;
	private Integer epsilonId;
	private String moduleKey;
	private String localTimeZone;
	private String attributeCode;
	private String attributeValue;
//	@Formula("SELECT COUNT(w.config_id) FROM winner_config_selection_reference w WHERE w.promotion_id = id")
	private Long winnerConfig;
	@ManyToOne
	private PromotionCluster promotionCluster;
	@OneToOne
	private Region region;
	private Integer maxLimit;
}