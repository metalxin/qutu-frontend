package cn.sdstudio.qutu.planning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "AI规划路线结果")
public class AIRouteVO {

	@Schema(description = "路线ID")
	private Long id;

	@Schema(description = "路线名称")
	private String name;

	@Schema(description = "出发城市")
	private String startCity;

	@Schema(description = "目的城市")
	private String endCity;

	@Schema(description = "行程天数")
	private Integer days;

	@Schema(description = "总距离")
	private String distance;

	@Schema(description = "总时长")
	private String totalTime;

	@Schema(description = "景点列表")
	private List<RouteSpotVO> spots;

	@Schema(description = "每日行程安排")
	private List<DayScheduleVO> schedule;

	@Schema(description = "路线概要")
	private RouteSummaryVO summary;

}
