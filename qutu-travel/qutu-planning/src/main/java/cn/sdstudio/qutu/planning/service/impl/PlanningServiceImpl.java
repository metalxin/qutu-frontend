package cn.sdstudio.qutu.planning.service.impl;

import cn.sdstudio.qutu.planning.config.AiProperties;
import cn.sdstudio.qutu.planning.dto.PlanningRequestDTO;
import cn.sdstudio.qutu.planning.service.OpenAiService;
import cn.sdstudio.qutu.planning.service.PlanningService;
import cn.sdstudio.qutu.planning.vo.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanningServiceImpl implements PlanningService {

	private final OpenAiService openAiService;

	private final AiProperties aiProperties;

	private final ObjectMapper objectMapper;

	private static final String SYSTEM_PROMPT = """
			你是旅行规划师。根据用户需求生成行程，只返回JSON，不要其他文字。

			JSON格式（所有字段平铺，不要嵌套）：
			{"name":"路线名","startCity":"出发城市","endCity":"目的城市","days":2,"distance":"100km","totalTime":"约16小时","schedule":[{"day":1,"date":"4月26日","spots":[{"id":1,"name":"景点名","day":1,"order":1,"duration":"2小时","startTime":"09:00","endTime":"11:00","rating":4.8,"price":"¥60","category":"自然风光","description":"简短描述","tips":"贴士","openTime":"08:00-18:00","address":"地址"}],"totalDuration":"6小时","totalDistance":"24km","meals":{"lunch":{"name":"餐厅","address":"地址"},"dinner":{"name":"餐厅","address":"地址"}},"hotel":{"name":"酒店","price":"¥299"}}],"summary":{"totalSpots":6,"totalDistance":"100km","estimatedCost":"¥900","bestSeason":"春秋"}}

			规则：auto每天3景点，relax每天2个，intensive每天4-5个，spots每天3个不含餐饮住宿。午餐12:00-13:30，晚餐18:00-19:30，每天09:00开始，景点间交通约30分钟。景点要真实，住宿仅非最后一天安排。
			""";

	@Override
	public AIRouteVO generateRoute(PlanningRequestDTO request) {
		String userPrompt = buildUserPrompt(request);

		log.info("开始生成规划路线: {} -> {}, {}天, 偏好: {}", request.getStartLocation(),
				request.getEndLocation(), request.getDays(), request.getPreference());

		try {
			String aiResponse = openAiService.chat(SYSTEM_PROMPT, userPrompt);
			log.info("AI返回结果长度: {}", aiResponse.length());
			log.info("AI返回前500字符: {}", aiResponse.length() > 500 ? aiResponse.substring(0, 500) + "..." : aiResponse);

			aiResponse = extractJson(aiResponse);
			log.info("JSON提取后长度: {}", aiResponse.length());

			ObjectMapper lenientMapper = JsonMapper.builder()
				.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
				.enable(JsonParser.Feature.ALLOW_TRAILING_COMMA)
				.enable(JsonParser.Feature.ALLOW_COMMENTS)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.build();

			JsonNode root = lenientMapper.readTree(aiResponse);
			AIRouteVO route = parseRoute(root);

			route.setId(System.currentTimeMillis());

			if (route.getName() == null || route.getName().isEmpty()) {
				String preferenceLabel = getPreferenceLabel(request.getPreference());
				route.setName(request.getStartLocation() + request.getDays() + "日" + preferenceLabel + "游");
			}
			if (route.getStartCity() == null) {
				route.setStartCity(request.getStartLocation());
			}
			if (route.getEndCity() == null) {
				route.setEndCity(request.getEndLocation());
			}
			if (route.getDays() == null) {
				route.setDays(request.getDays());
			}

			if (route.getSpots() == null && route.getSchedule() != null) {
				List<RouteSpotVO> allSpots = new ArrayList<>();
				for (DayScheduleVO daySchedule : route.getSchedule()) {
					if (daySchedule.getSpots() != null) {
						allSpots.addAll(daySchedule.getSpots());
					}
				}
				route.setSpots(allSpots);
			}

			return route;
		}
		catch (Exception e) {
			log.error("AI规划生成失败，使用降级方案: {}", e.getMessage());
			return generateFallbackRoute(request);
		}
	}

	private String buildUserPrompt(PlanningRequestDTO request) {
		StringBuilder sb = new StringBuilder();
		sb.append("请为我规划一次旅行：\n");
		sb.append("出发地：").append(request.getStartLocation()).append("\n");
		sb.append("目的地：").append(request.getEndLocation()).append("\n");

		if (request.getWaypoints() != null && !request.getWaypoints().isEmpty()) {
			sb.append("途经城市：").append(String.join("、", request.getWaypoints())).append("\n");
		}

		sb.append("行程天数：").append(request.getDays()).append("天\n");

		String preferenceDesc = switch (request.getPreference()) {
			case "relax" -> "休闲模式（每天2个景点，节奏较慢）";
			case "intensive" -> "特种兵模式（紧凑安排，一天玩遍更多景点）";
			case "spots" -> "只规划景点（不包含餐厅住宿推荐）";
			default -> "自动推荐（AI根据景点评分和距离智能规划）";
		};
		sb.append("规划偏好：").append(preferenceDesc).append("\n");

		if (request.getTransport() != null) {
			String transportDesc = switch (request.getTransport()) {
				case "driving" -> "自驾";
				case "public" -> "公共交通";
				case "walking" -> "步行";
				case "cycling" -> "骑行";
				default -> "自驾";
			};
			sb.append("交通方式：").append(transportDesc).append("\n");
		}

		sb.append("\n请生成详细的行程规划，严格按照指定的JSON格式返回。");

		return sb.toString();
	}

	private AIRouteVO generateFallbackRoute(PlanningRequestDTO request) {
		AIRouteVO route = new AIRouteVO();
		route.setId(System.currentTimeMillis());

		String preferenceLabel = getPreferenceLabel(request.getPreference());
		route.setName(request.getStartLocation() + request.getDays() + "日" + preferenceLabel + "游");
		route.setStartCity(request.getStartLocation());
		route.setEndCity(request.getEndLocation());
		route.setDays(request.getDays());
		route.setDistance(request.getDays() * 50 + "km");
		route.setTotalTime("约" + request.getDays() * 8 + "小时");

		int spotsPerDay = switch (request.getPreference()) {
			case "relax" -> 2;
			case "intensive" -> 4;
			default -> 3;
		};

		List<RouteSpotVO> allSpots = new ArrayList<>();
		List<DayScheduleVO> scheduleList = new ArrayList<>();
		LocalDate startDate = LocalDate.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M月d日");

		int spotCounter = 1;
		for (int day = 1; day <= request.getDays(); day++) {
			List<RouteSpotVO> daySpots = new ArrayList<>();
			int currentHour = 9;

			for (int order = 1; order <= spotsPerDay; order++) {
				RouteSpotVO spot = new RouteSpotVO();
				spot.setId(spotCounter * 100 + day * 10 + order);
				spot.setName(request.getEndLocation() + "景点" + spotCounter);
				spot.setDay(day);
				spot.setOrder(order);
				spot.setDuration("2小时");

				String startTime = String.format("%02d:00", currentHour);
				int endHour = currentHour + 2;
				String endTime = String.format("%02d:00", endHour > 12 ? endHour : endHour);
				spot.setStartTime(startTime);
				spot.setEndTime(endTime);

				SpotInfoVO spotInfo = new SpotInfoVO();
				spotInfo.setId(spot.getId());
				spotInfo.setName(spot.getName());
				spotInfo.setRating(4.5);
				spotInfo.setDuration("2小时");
				spotInfo.setPrice("¥50");
				spotInfo.setCategory("自然风光");
				spotInfo.setDescription("推荐游览的景点");
				spotInfo.setTips("建议提前预约");
				spotInfo.setOpenTime("08:00-18:00");
				spotInfo.setAddress(request.getEndLocation() + "市中心");
				spotInfo.setLatitude(30.0 + Math.random());
				spotInfo.setLongitude(120.0 + Math.random());
				spot.setSpotInfo(spotInfo);

				daySpots.add(spot);
				allSpots.add(spot);
				spotCounter++;

				currentHour = endHour + 1;
				if (currentHour >= 12 && currentHour < 14) {
					currentHour = 14;
				}
			}

			DayScheduleVO daySchedule = new DayScheduleVO();
			daySchedule.setDay(day);
			daySchedule.setDate(startDate.plusDays(day - 1).format(fmt));
			daySchedule.setSpots(daySpots);
			daySchedule.setTotalDuration(spotsPerDay * 2 + "小时");
			daySchedule.setTotalDistance(spotsPerDay * 8 + "km");

			if (!"spots".equals(request.getPreference())) {
				DayScheduleVO.MealsVO meals = new DayScheduleVO.MealsVO();
				MealVO lunch = new MealVO();
				lunch.setName("当地特色餐厅");
				lunch.setAddress(request.getEndLocation() + "美食街");
				meals.setLunch(lunch);

				MealVO dinner = new MealVO();
				dinner.setName("当地风味馆");
				dinner.setAddress(request.getEndLocation() + "旅游区");
				meals.setDinner(dinner);
				daySchedule.setMeals(meals);

				if (day < request.getDays()) {
					HotelVO hotel = new HotelVO();
					hotel.setName("舒适酒店");
					hotel.setPrice("¥299");
					daySchedule.setHotel(hotel);
				}
			}

			scheduleList.add(daySchedule);
		}

		route.setSpots(allSpots);
		route.setSchedule(scheduleList);

		RouteSummaryVO summary = new RouteSummaryVO();
		summary.setTotalSpots(allSpots.size());
		summary.setTotalDistance(request.getDays() * 50 + "km");
		summary.setEstimatedCost("¥" + (allSpots.size() * 50 + request.getDays() * 300));
		summary.setBestSeason("春秋季节");
		route.setSummary(summary);

		return route;
	}

	private String getPreferenceLabel(String preference) {
		return switch (preference) {
			case "intensive" -> "特种兵";
			case "relax" -> "休闲";
			case "spots" -> "精华景点";
			default -> "精华";
		};
	}

	@Override
	public List<PreferenceOptionVO> getPreferenceOptions() {
		List<PreferenceOptionVO> options = new ArrayList<>();
		options.add(createOption("auto", "自动推荐", "AI根据景点评分和距离智能规划"));
		options.add(createOption("spots", "只规划景点", "只包含景点，不包含餐厅住宿"));
		options.add(createOption("relax", "休闲模式", "每天安排2-3个景点，节奏较慢"));
		options.add(createOption("intensive", "特种兵模式", "紧凑安排，一天玩遍更多景点"));
		return options;
	}

	@Override
	public List<TransportModeVO> getTransportModes() {
		List<TransportModeVO> modes = new ArrayList<>();
		modes.add(createTransport("driving", "自驾", "🚗"));
		modes.add(createTransport("public", "公共交通", "🚇"));
		modes.add(createTransport("walking", "步行", "🚶"));
		modes.add(createTransport("cycling", "骑行", "🚲"));
		return modes;
	}

	@Override
	public List<String> getHotCities() {
		return List.of("北京", "上海", "广州", "深圳", "杭州", "南京", "成都", "重庆", "西安", "武汉", "长沙", "苏州", "厦门",
				"青岛", "大连", "三亚", "丽江", "拉萨", "合肥", "黄山", "桂林", "张家界", "九寨沟", "敦煌");
	}

	private PreferenceOptionVO createOption(String id, String name, String desc) {
		PreferenceOptionVO option = new PreferenceOptionVO();
		option.setId(id);
		option.setName(name);
		option.setDesc(desc);
		return option;
	}

	private TransportModeVO createTransport(String id, String name, String icon) {
		TransportModeVO mode = new TransportModeVO();
		mode.setId(id);
		mode.setName(name);
		mode.setIcon(icon);
		return mode;
	}

	private String extractJson(String text) {
		String cleaned = text.trim();

		if (cleaned.startsWith("```json")) {
			cleaned = cleaned.substring(7);
		}
		else if (cleaned.startsWith("```")) {
			cleaned = cleaned.substring(3);
		}
		if (cleaned.endsWith("```")) {
			cleaned = cleaned.substring(0, cleaned.length() - 3);
		}
		cleaned = cleaned.trim();

		// 找到第一个 {
		int firstBrace = cleaned.indexOf('{');
		if (firstBrace > 0) {
			cleaned = cleaned.substring(firstBrace);
		}

		// 使用括号计数找到匹配的最后一个 }
		int braceCount = 0;
		int lastBrace = -1;
		for (int i = 0; i < cleaned.length(); i++) {
			char c = cleaned.charAt(i);
			if (c == '{') {
				braceCount++;
			}
			else if (c == '}') {
				braceCount--;
				if (braceCount == 0) {
					lastBrace = i;
					break;
				}
			}
		}

		if (lastBrace >= 0) {
			cleaned = cleaned.substring(0, lastBrace + 1);
		}

		log.info("JSON提取完成, 长度: {}", cleaned.length());
		return cleaned;
	}

	private AIRouteVO parseRoute(JsonNode root) {
		AIRouteVO route = new AIRouteVO();
		route.setName(getStr(root, "name"));
		route.setStartCity(getStr(root, "startCity"));
		route.setEndCity(getStr(root, "endCity"));
		route.setDays(getInt(root, "days"));
		route.setDistance(getStr(root, "distance"));
		route.setTotalTime(getStr(root, "totalTime"));

		if (root.has("schedule") && root.get("schedule").isArray()) {
			List<DayScheduleVO> scheduleList = new ArrayList<>();
			for (JsonNode dayNode : root.get("schedule")) {
				DayScheduleVO daySchedule = new DayScheduleVO();
				daySchedule.setDay(getInt(dayNode, "day"));
				daySchedule.setDate(getStr(dayNode, "date"));
				daySchedule.setTotalDuration(getStr(dayNode, "totalDuration"));
				daySchedule.setTotalDistance(getStr(dayNode, "totalDistance"));

				if (dayNode.has("spots") && dayNode.get("spots").isArray()) {
					List<RouteSpotVO> spots = new ArrayList<>();
					for (JsonNode spotNode : dayNode.get("spots")) {
						RouteSpotVO spot = new RouteSpotVO();
						spot.setId(getInt(spotNode, "id"));
						spot.setName(getStr(spotNode, "name"));
						spot.setDay(getInt(spotNode, "day"));
						spot.setOrder(getInt(spotNode, "order"));
						spot.setDuration(getStr(spotNode, "duration"));
						spot.setStartTime(getStr(spotNode, "startTime"));
						spot.setEndTime(getStr(spotNode, "endTime"));

						SpotInfoVO info = new SpotInfoVO();
						if (spotNode.has("spotInfo") && spotNode.get("spotInfo").isObject()) {
							JsonNode infoNode = spotNode.get("spotInfo");
							info.setId(getInt(infoNode, "id"));
							info.setName(getStr(infoNode, "name"));
							info.setDuration(getStr(infoNode, "duration"));
							info.setRating(getDouble(infoNode, "rating"));
							info.setPrice(getStr(infoNode, "price"));
							info.setCategory(getStr(infoNode, "category"));
							info.setDescription(getStr(infoNode, "description"));
							info.setTips(getStr(infoNode, "tips"));
							info.setOpenTime(getStr(infoNode, "openTime"));
							info.setAddress(getStr(infoNode, "address"));
						}
						else {
							info.setId(spot.getId());
							info.setName(spot.getName());
							info.setDuration(spot.getDuration());
							info.setRating(getDouble(spotNode, "rating"));
							info.setPrice(getStr(spotNode, "price"));
							info.setCategory(getStr(spotNode, "category"));
							info.setDescription(getStr(spotNode, "description"));
							info.setTips(getStr(spotNode, "tips"));
							info.setOpenTime(getStr(spotNode, "openTime"));
							info.setAddress(getStr(spotNode, "address"));
						}
						spot.setSpotInfo(info);

						spots.add(spot);
					}
					daySchedule.setSpots(spots);
				}

				if (dayNode.has("meals")) {
					DayScheduleVO.MealsVO meals = new DayScheduleVO.MealsVO();
					JsonNode mealsNode = dayNode.get("meals");
					if (mealsNode.has("lunch")) {
						MealVO lunch = new MealVO();
						lunch.setName(getStr(mealsNode.get("lunch"), "name"));
						lunch.setAddress(getStr(mealsNode.get("lunch"), "address"));
						meals.setLunch(lunch);
					}
					if (mealsNode.has("dinner")) {
						MealVO dinner = new MealVO();
						dinner.setName(getStr(mealsNode.get("dinner"), "name"));
						dinner.setAddress(getStr(mealsNode.get("dinner"), "address"));
						meals.setDinner(dinner);
					}
					daySchedule.setMeals(meals);
				}

				if (dayNode.has("hotel")) {
					JsonNode hotelNode = dayNode.get("hotel");
					if (hotelNode != null && !hotelNode.isNull()) {
						HotelVO hotel = new HotelVO();
						hotel.setName(getStr(hotelNode, "name"));
						hotel.setPrice(getStr(hotelNode, "price"));
						daySchedule.setHotel(hotel);
					}
				}

				scheduleList.add(daySchedule);
			}
			route.setSchedule(scheduleList);
		}

		if (root.has("summary")) {
			JsonNode sumNode = root.get("summary");
			RouteSummaryVO summary = new RouteSummaryVO();
			summary.setTotalSpots(getInt(sumNode, "totalSpots"));
			summary.setTotalDistance(getStr(sumNode, "totalDistance"));
			summary.setEstimatedCost(getStr(sumNode, "estimatedCost"));
			summary.setBestSeason(getStr(sumNode, "bestSeason"));
			route.setSummary(summary);
		}

		return route;
	}

	private String getStr(JsonNode node, String field) {
		if (node.has(field) && !node.get(field).isNull()) {
			return node.get(field).asText();
		}
		return null;
	}

	private Integer getInt(JsonNode node, String field) {
		if (node.has(field) && !node.get(field).isNull()) {
			return node.get(field).asInt();
		}
		return null;
	}

	private Double getDouble(JsonNode node, String field) {
		if (node.has(field) && !node.get(field).isNull()) {
			return node.get(field).asDouble();
		}
		return null;
	}

}
