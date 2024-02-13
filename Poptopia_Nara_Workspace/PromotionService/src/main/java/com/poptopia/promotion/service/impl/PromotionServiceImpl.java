package com.poptopia.promotion.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.poptopia.promotion.APIResponses.ApiListResponse;
import com.poptopia.promotion.constants.CodeConstants;
import com.poptopia.promotion.entity.Promotion;
import com.poptopia.promotion.entity.PromotionCluster;
import com.poptopia.promotion.entity.Region;
import com.poptopia.promotion.entity.WinnerConfig;
import com.poptopia.promotion.exceptions.ApiException;
import com.poptopia.promotion.models.MechanicRequest;
import com.poptopia.promotion.models.PromotionCreateRequest;
import com.poptopia.promotion.models.PromotionRequest;
import com.poptopia.promotion.models.PromotionResponse;
import com.poptopia.promotion.models.SettingRequest;
import com.poptopia.promotion.repository.PromotionClusterRepository;
import com.poptopia.promotion.repository.PromotionRepository;
import com.poptopia.promotion.repository.RegionRepository;
import com.poptopia.promotion.repository.winnerConfigRepository;
import com.poptopia.promotion.service.PromotionService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class PromotionServiceImpl implements PromotionService {
	
	@Autowired
	private PromotionRepository promotionRepo;
	
	@Autowired
	private RegionRepository regionRepo;
	
	@Autowired
	private PromotionClusterRepository promotionClusterRepo;
	
	@Autowired
    private	winnerConfigRepository winnerConfigRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public ApiListResponse<PromotionResponse> createPromotion(PromotionRequest promotionCreateRequest) {
		createPromotionInputsValidation(promotionCreateRequest);
		createPromotionDuplicateValuesCheck(promotionCreateRequest.getPromotions());
		clusterIdValidation(promotionCreateRequest.getClusterId());
		List<PromotionResponse> promotionResponseList = new ArrayList<>();
		if (promotionCreateRequest.getPromotions().isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400,
					String.format("Required Promotions are Empty ", promotionResponseList));
		}
		List<PromotionCreateRequest> promotions = promotionCreateRequest.getPromotions();
		for (PromotionCreateRequest promotionRequest : promotions) {

			promotionRegionIdValidation(promotionRequest.getRegionId());
			promotionModuleKeyValidation(promotionRequest.getModuleKey());
			promotionNameValidation(promotionRequest.getPromotionName());
			promotionEpsilonIdValidation(promotionRequest.getEpsilonId());

		}
		MechanicRequest mechanic = promotionCreateRequest.getMechanic();
		final String df = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat simpleDateFormate = new SimpleDateFormat(df);
		Date mechanicStartDate = null;
		Date mechanicEndDate = null;
		try {
			mechanicStartDate = simpleDateFormate.parse(getRequiredStringDate(mechanic.getStartDate()));
			mechanicEndDate = simpleDateFormate.parse(getRequiredStringDate(mechanic.getEndDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		LocalDateTime reqMechanicStartDateTime = LocalDateTime.ofInstant(Instant.parse(mechanic.getStartDate()), ZoneId.of(ZoneOffset.UTC.getId()));
		LocalDateTime reqMechanicEndDateTime = LocalDateTime.ofInstant(Instant.parse(mechanic.getEndDate()), ZoneId.of(ZoneOffset.UTC.getId()));
		mechanicValidattion(mechanic.getType(), mechanic.getStartDate(), mechanic.getEndDate());
		String settingsValue = "";
		List<SettingRequest> settings = promotionCreateRequest.getSettings();
		for (SettingRequest promotionSetting : settings) {
			promotionSettingValidation(promotionSetting.getName(), promotionSetting.getValue());
			settingsValue = promotionSetting.getValue();
		}
		Set<Promotion> setPromotions = new HashSet<>();
		List<Promotion> promotionList = new ArrayList<>();
		Promotion savedPromotion = new Promotion();
		Date date = new Date();
		LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		for (PromotionCreateRequest promotionRequest : promotions) {
			Promotion promotion = new Promotion();
			Region region = regionRepo.findById(promotionRequest.getRegionId())
					.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, 400,
							String.format("Region with Region id \'%d\' not found", promotionRequest.getRegionId())));

			promotion.setModuleKey(promotionRequest.getModuleKey());
			promotion.setName(promotionRequest.getPromotionName());
			promotion.setEpsilonId(promotionRequest.getEpsilonId());
			promotion.setRegion(region);
			promotion.setLocalTimeZone(promotionRequest.getLocalTimeZone());
			promotion.setStartDate(reqMechanicStartDateTime);
			promotion.setEndDate(reqMechanicEndDateTime);
			promotion.setAttributeCode(mechanic.getAttributeCode());
			promotion.setAttributeValue(mechanic.getAttributeValue());
			promotion.setCreatedDate(LocalDateTime.now());
			promotion.setModifiedDate(LocalDateTime.now());
			promotion.setMaxLimit(1);
//			promotion.setCreatedDateTime(localDateTime);
//			promotion.setModifiedDateTime(localDateTime);
			Optional<PromotionCluster> findById = promotionClusterRepo.findById(promotionCreateRequest.getClusterId());
			if (findById.isPresent()) {
				promotion.setPromotionCluster(findById.get());
			} else {
				throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
						.format("Cluster with ClusterId \'%d\'' is NOT exists. ",
								promotionCreateRequest.getClusterId()));
			}
			savedPromotion = promotionRepo.save(promotion);

			promotionList.add(savedPromotion);
			setPromotions.add(savedPromotion);
		}

		if (mechanic.getType().equalsIgnoreCase(CodeConstants.WM.getStatus())) {
			wmStartDateCalculation(mechanicStartDate, mechanicEndDate, setPromotions, settingsValue);
		}
		promotionResponseList = promotionResponseSetUp(promotionList, mechanic.getStartDate(), mechanic.getEndDate());

		return new ApiListResponse<>("All promotions :--- ", promotionResponseList, promotionResponseList.size());
	}
	
	public static String getRequiredStringDate(String str) {
		String[] ts = str.split("T");
		return ts[0] + " " + ts[1].substring(0, 8);
	}
	
	public static int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}
	
	public void wmStartDateCalculation(Date mechanicStartDate, Date mechanicEndDate, Set<Promotion> setPromotions,
			String settingsValue) {

		long startDateMillis = mechanicStartDate.getTime();
		long endDateMillis = mechanicEndDate.getTime();
		int periodTime = (int) ((endDateMillis - startDateMillis)
				/ Integer.parseInt(settingsValue));
		for (int i = 0; i < Integer.parseInt(settingsValue); i++) {
			int randomNumber = getRandomNumber(1, periodTime);
			long prizetime = startDateMillis + randomNumber;
			Date prizeTimeDate = new Date(prizetime);
			long endtime = endDateMillis;
			Date resend = new Date(endtime);
			startDateMillis = startDateMillis + periodTime;
			WinnerConfig winnerConfig = winnerConfigSetUp(setPromotions, prizeTimeDate, mechanicEndDate);
			winnerConfigRepo.save(winnerConfig);
		}

	}
	
	public WinnerConfig winnerConfigSetUp(Set<Promotion> setPromotions, Date dateOfPromotion, Date endDate) {
		LocalDateTime winnerConfigStartDateTime = dateOfPromotion.toInstant().atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		LocalDateTime winnerConfigEndDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		WinnerConfig winnerConfig = new WinnerConfig();
		winnerConfig.setMaxWinner(0);
//		winnerConfig.setLimit(1);
		winnerConfig.setWinProbability(0);
		winnerConfig.setWinStep(0);
		winnerConfig.setEndTime(winnerConfigEndDateTime);
		winnerConfig.setStartTime(winnerConfigStartDateTime);
		winnerConfig.setPromotions(setPromotions);
		winnerConfig.setCreatedDate(LocalDateTime.now());
		winnerConfig.setModifiedDate(LocalDateTime.now());
		winnerConfig.setStartDate(winnerConfigStartDateTime);
		winnerConfig.setEndDate(winnerConfigEndDateTime);
		return winnerConfig;
	}

	public List<PromotionResponse> promotionResponseSetUp(List<Promotion> savedPromotions, String mechanicStartDate,
			String mechanicEndDate) {
		List<PromotionResponse> promotionResponseList = new ArrayList<>();

		for (Promotion savedPromotion : savedPromotions) {
			PromotionResponse promotionResponse = new PromotionResponse();
			promotionResponse.setId(savedPromotion.getId());
			promotionResponse.setCreateDate(LocalDateTime.now());
			promotionResponse.setModifiedDate(LocalDateTime.now());
			promotionResponse.setName(savedPromotion.getName());
			promotionResponse.setEpsilonId(savedPromotion.getEpsilonId());
			promotionResponse.setStartDate(mechanicStartDate);
			promotionResponse.setEndDate(mechanicEndDate);
			promotionResponse.setMaxLimit(1);
			promotionResponse.setModuleKey(savedPromotion.getModuleKey());
			promotionResponse.setLocalTimeZone(savedPromotion.getLocalTimeZone());
			promotionResponse.setAttributeCode(savedPromotion.getAttributeCode());
			promotionResponse.setAttributeValue(savedPromotion.getAttributeValue());
			Query query = entityManager.createNativeQuery(
					" SELECT COUNT(w.config_id) FROM Winner_Selection_Config_Reference w WHERE w.promotion_id ="
							+ savedPromotion.getId());
			List<Long> resultList = (List<Long>) query.getResultList();
			if (!resultList.isEmpty()) {
				promotionResponse.setWinnerconfig(resultList.get(0));
			}
			promotionResponse.setRegion(savedPromotion.getRegion());
			promotionResponseList.add(promotionResponse);
		}
		return promotionResponseList;
	}
	
	public void createPromotionInputsValidation(PromotionRequest promotionCreateRequest) {
		if (promotionCreateRequest.getClusterId() == null
				|| promotionCreateRequest.getClusterId().equals(null)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String.format(
					"Malfunction Request. Promotion with Cluster Details must NOT be null."));
		} else if (promotionCreateRequest.getPromotions() == null
				|| promotionCreateRequest.getPromotions().equals(null)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String.format(
					"Malfunction Request. Promotion with promotion Array Details must NOT be null."));
		} else if (promotionCreateRequest.getMechanic() == null
				|| promotionCreateRequest.getMechanic().equals(null)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String.format(
					"Malfunction Request. Promotion with mechanic Details must NOT be null."));
		} else if (promotionCreateRequest.getSettings() == null
				|| promotionCreateRequest.getSettings().equals(null)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String.format(
					"Malfunction Request. Promotion with setting Array Details must NOT be null."));
		}
	}
	
	public static void createPromotionDuplicateValuesCheck(List<PromotionCreateRequest> promotionRequestsList) {
		List<Map.Entry<String, Long>> namelist = promotionRequestsList.stream().map(PromotionCreateRequest::getPromotionName)
				.collect(Collectors.toList()).stream().collect(Collectors.groupingBy(
						Function.identity(),
						Collectors.counting()))
				.entrySet().stream().collect(Collectors.toList()).stream().filter(x -> x.getValue() > 1)
				.collect(Collectors.toList());

		List<Map.Entry<String, Long>> moduleKeylist = promotionRequestsList.stream().map(PromotionCreateRequest::getModuleKey)
				.collect(Collectors.toList()).stream().collect(Collectors.groupingBy(
						Function.identity(),
						Collectors.counting()))
				.entrySet().stream().collect(Collectors.toList()).stream().filter(x -> x.getValue() > 1)
				.collect(Collectors.toList());

		List<Map.Entry<Integer, Long>> epsilonIdslist = promotionRequestsList.stream()
				.map(PromotionCreateRequest::getEpsilonId).collect(Collectors.toList()).stream()
				.collect(Collectors.groupingBy(
						Function.identity(),
						Collectors.counting()))
				.entrySet().stream().collect(Collectors.toList()).stream().filter(x -> x.getValue() > 1)
				.collect(Collectors.toList());

		if (!namelist.isEmpty() && !moduleKeylist.isEmpty() && !epsilonIdslist.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("Repeated promotion Details are ::  promotionNames are :\'%s\' and promotion ModuleKeys are : \'%s\' and promotion EpsilonIds are : \'%s\' Please provide valid details to proceed createPromotions. ",
							namelist, moduleKeylist, epsilonIdslist));
		} else if (!namelist.isEmpty() && !moduleKeylist.isEmpty() && epsilonIdslist.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("Repeated promotion Details are ::  promotionNames are :\'%s\' and promotion ModuleKeys are :\'%s\' Please provide valid details to proceed createPromotions. ",
							namelist, moduleKeylist));
		} else if (namelist.isEmpty() && !moduleKeylist.isEmpty() && epsilonIdslist.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("Repeated promotion Details are :: promotion ModuleKeys are : \'%s\'  Please provide valid details to proceed createPromotions. ",
							moduleKeylist));
		} else if (!namelist.isEmpty() && moduleKeylist.isEmpty() && epsilonIdslist.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("Repeated promotion Details are ::  promotionNames are :\'%s\'  Please provide valid details to proceed createPromotions. ",
							namelist));
		} else if (namelist.isEmpty() && moduleKeylist.isEmpty() && !epsilonIdslist.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("Repeated promotion Details are ::  promotion EpsilonIds are : \'%s\' Please provide valid details to proceed createPromotions. ",
							epsilonIdslist));
		} else if (!namelist.isEmpty() && moduleKeylist.isEmpty() && !epsilonIdslist.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("Repeated promotion Details are ::  promotionNames are :\'%s\' and promotion EpsilonIds are : \'%s\' Please provide valid details to proceed createPromotions. ",
							namelist, epsilonIdslist));
		} else if (namelist.isEmpty() && !moduleKeylist.isEmpty() && !epsilonIdslist.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("Repeated promotion Details are ::  promotion ModuleKeys are : \'%s\' and promotion EpsilonIds are : \'%s\' Please provide valid details to proceed createPromotions. ",
							moduleKeylist, epsilonIdslist));
		}
	}
	
	public void clusterIdValidation(Integer promotionClusterId) {
		if (promotionClusterId == null
				|| promotionClusterId == 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("ClusterId is \'%d\' now. It Must NOT be Zero OR null OR EMPTY OR Blank",
							promotionClusterId));
		} else {
			promotionClusterRepo.findById(promotionClusterId)
					.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, 400,
							String.format("Cluster with ClusterId \'%d\' not found", promotionClusterId)));
		}
	}
	
	public void promotionRegionIdValidation(Integer promotionRegionId) {
		if (promotionRegionId == null
				|| promotionRegionId == 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("RegionId is \'%d\' now. It Must NOT be Zero OR null OR EMPTY OR Blank",
							promotionRegionId));
		} else {
			regionRepo.findById(promotionRegionId)
					.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, 400,
							String.format("Region with Region id \'%d\' not found", promotionRegionId)));
		}
	}
	
	public void promotionSettingValidation(String settingName, String settingValue) {
		if (settingName == null || settingValue == null
				|| settingName.isEmpty() || settingName.isBlank()
				|| settingValue.isEmpty() || settingValue.isBlank()
				|| settingValue.equals("0") || settingValue.equals(null)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400,
					String.format(
							"Setting name \'%s\' is now and value is \'%s\' now. It should NOT be Zero OR NULL OR EMPTY OR BLANK",
							settingName, settingValue));
		} else if (settingName != null
				|| !settingName.isEmpty() || !settingName.isBlank()
				|| !settingValue.isEmpty() || !settingValue.isBlank()
				|| !settingValue.equals("0") || !settingValue.equals(null)) {
			if (settingName.equals("moments") || settingName.equals("prize")) {
			} else {
				throw new ApiException(HttpStatus.BAD_REQUEST, 400,
						String.format(
								"Setting with Name \'%s\' is now. NOT matching with \'moments\' or \'prize\' ",
								settingName));
			}
		}
	}
	
	public void promotionEpsilonIdValidation(Integer promotionEpsilonId) {
		if (promotionEpsilonId == null
				|| promotionEpsilonId.equals(null)
				|| promotionEpsilonId == 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String.format(
					"Promotion with EpsilonId is \'%d\' now. It Should NOT be Zero or null or Empty ",
					promotionEpsilonId));
		} else if (promotionEpsilonId != 0) {
			boolean contains = false;
			Range<Integer> open = Range.open(0, 2147483647);
			try {
				contains = open.contains(promotionEpsilonId);
			} catch (Exception ex) {
				throw new ApiException(HttpStatus.BAD_REQUEST, 400, String.format(
						"Promotion with EpsilonId is \'%d\' now. Input value must be Integer value and it should be between (1 to 2147483647) ",
						promotionEpsilonId));
			}

		} else if (!promotionEpsilonId.equals(null)
				|| promotionEpsilonId != 0) {
			Optional<Promotion> findByEpsilonId = promotionRepo.findByEpsilonId(promotionEpsilonId);
			if (findByEpsilonId.isPresent()) {
				throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
						.format("Promotion with EpsilonId \'%d\' is already Exists with PromotionId \'%d\' ",
								promotionEpsilonId, findByEpsilonId.get().getId()));
			}
		}
	}
	
	public void promotionModuleKeyValidation(String promotionModuleKey) {
		if (promotionModuleKey == null
				|| promotionModuleKey.equals(null)
				|| promotionModuleKey.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String.format(
					"Promotion with ModuleKey is \'%s\' now. It Should NOT be null or Empty ", promotionModuleKey));
		} else if (promotionModuleKey != null
				|| !promotionModuleKey.isEmpty()
				|| !promotionModuleKey.equals(null)
				|| !promotionModuleKey.isBlank()) {
			Optional<Promotion> findByModuleKey = promotionRepo.findByModuleKey(promotionModuleKey);
			if (findByModuleKey.isPresent()) {
				throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
						.format("Promotion with ModuleKey \'%s\' is already Exists with PromotionId \'%d\'",
								promotionModuleKey, findByModuleKey.get().getId()));
			}
		}
	}
	
	public void promotionNameValidation(String promotionName) {
		if (promotionName == null
				|| promotionName.equals(null)
				|| promotionName.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String.format(
					"Promotion with Name is \'%s\' now. It Should NOT be null or Empty ", promotionName));
		} else if (!promotionName.isEmpty()
				|| !promotionName.equals(null)
				|| !promotionName.isBlank()) {
			Optional<Promotion> findByName = promotionRepo.findByName(promotionName);
			if (findByName.isPresent()) {
				throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
						.format("Promotion with Name \'%s\' is already Exists with PromotionId \'%d\'", promotionName,
								findByName.get().getId()));
			}
		}
	}
	
	public void mechanicValidattion(String mechanicType, String mechanicStartDate, String mechanicEndDate) {
		if (!mechanicStartDate.contains("T") || !mechanicStartDate.contains("Z")
				|| !mechanicEndDate.contains("T") || !mechanicEndDate.contains("Z")) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String.format(
					"Mechanic with dates is must be JSON date format like (yyyy-MM-ddTHH:mm:ss.SSSZ)."));
		}
		LocalDateTime reqStartDateTime = LocalDateTime.ofInstant(Instant.parse(mechanicStartDate),
				ZoneId.of(ZoneOffset.UTC.getId()));
		LocalDateTime reqEndDateTime = LocalDateTime.ofInstant(Instant.parse(mechanicEndDate),
				ZoneId.of(ZoneOffset.UTC.getId()));
		if (mechanicType == null
				|| mechanicType.isBlank()
				|| mechanicType.isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400,
					String.format("Mechanic with Type MUST NOT be null OR Empty"));
		} else if (mechanicEndDate == null
				|| mechanicStartDate == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400,
					String.format("Mechanic with StartDate and EndDate MUST NOT be null OR Empty"));
		} else if (!(isDatePastTodayFuture(mechanicStartDate))) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("Mechanic with Start Date \'%s\'  MUST NOT be past dates.", mechanicStartDate));
		} else if (!(isDatePastTodayFuture(mechanicEndDate))) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("Mechanic with End Date \'%s\'  MUST NOT be past dates.", mechanicEndDate));
		} else if (!(reqStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < reqEndDateTime
				.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400, String
					.format("Mechanic with End Date \'%s\' must be Grater Than Start Date \'%s\'", mechanicEndDate,
							mechanicStartDate));
		} else if (mechanicType.equalsIgnoreCase(CodeConstants.WM.getStatus())
				|| mechanicType.equalsIgnoreCase(CodeConstants.TOS.getStatus())
				|| mechanicType.equalsIgnoreCase(CodeConstants.POOL.getStatus())) {
		} else {
			throw new ApiException(HttpStatus.BAD_REQUEST, 400,
					String.format("Mechanic with Type \'%s\' is NOT matching with \'wm\' or \'tos\' or \'pool\' ",
							mechanicType));
		}
	}
	
	public static boolean isDatePastTodayFuture(final String date) {
		boolean flag = false;
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime reqStartDateTime = LocalDateTime.ofInstant(Instant.parse(date),
				ZoneId.of(ZoneOffset.UTC.getId()));
		if (reqStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < currentDateTime
				.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}
}