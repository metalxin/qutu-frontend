package cn.sdstudio.qutu.planning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "路线概要")
public class RouteSummaryVO {

	@Schema(description = "景点总数")
	private Integer totalSpots;

	@Schema(description = "总距离")
	private String totalDistance;

	@Schema(description = "预估花费")
	private String estimatedCost;

	@Schema(description = "最佳季节")
	private String bestSeason;

}
