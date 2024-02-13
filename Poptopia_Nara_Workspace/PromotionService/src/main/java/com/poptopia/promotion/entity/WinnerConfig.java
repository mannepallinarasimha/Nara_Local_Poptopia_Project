package com.poptopia.promotion.entity;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="winner_selection_config")
@NamedQueries(value = {
		  
		 @NamedQuery(name = "WinnerConfig.findWinnerConfigFromDB", query ="SELECT wc FROM WinnerConfig wc WHERE wc.promotion.id =:promoId")})
public class WinnerConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;
	@ManyToOne
	private Promotion promotion;
	
	@ManyToMany
	@JoinTable(
			name="winner_selection_config_reference",
			joinColumns = @JoinColumn(name="config_id"),
			inverseJoinColumns = @JoinColumn(name="promotion_id")
			
			)
	private Set<Promotion> promotions;
	private Integer maxWinner;
//	private Integer limit;
	private Integer winProbability;
	private Integer winStep;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
}
