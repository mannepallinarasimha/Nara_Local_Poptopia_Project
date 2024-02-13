package com.poptopia.promotion.models;

import java.time.LocalDateTime;
import org.hibernate.annotations.Formula;
import com.poptopia.promotion.entity.Region;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponse {
	
	private LocalDateTime createDate;
	private LocalDateTime modifiedDate;
	private Integer id;
	private String name;
	private Integer epsilonId;
	private String startDate;
	private String endDate;
	private Integer maxLimit;
	private String moduleKey;
	private String localTimeZone;
	private String attributeCode;
	private String attributeValue;
	@Formula("SELECT COUNT(w.config_id) FROM winner_config_selection_reference w WHERE w.promotion_id = id")
	private Long winnerconfig;
	private Region region;

}
