package cn.sdstudio.qutu.planning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "每日行程")
public class DayScheduleVO {

	@Schema(description = "第几天")
	private Integer day;

	@Schema(description = "日期")
	private String date;

	@Schema(description = "当天景点列表")
	private List<RouteSpotVO> spots;

	@Schema(description = "总游玩时长")
	private String totalDuration;

	@Schema(description = "总距离")
	private String totalDistance;

	@Schema(description = "餐饮推荐")
	private MealsVO meals;

	@Schema(description = "住宿推荐")
	private HotelVO hotel;

	@Data
	@Schema(description = "餐饮推荐")
	public static class MealsVO {

		@Schema(description = "午餐推荐")
		private MealVO lunch;

		@Schema(description = "晚餐推荐")
		private MealVO dinner;

	}

}
