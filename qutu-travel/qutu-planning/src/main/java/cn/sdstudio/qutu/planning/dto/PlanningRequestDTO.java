package cn.sdstudio.qutu.planning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "智能规划请求参数")
public class PlanningRequestDTO {

	@Schema(description = "出发地城市", example = "合肥市")
	private String startLocation;

	@Schema(description = "目的地城市", example = "杭州市")
	private String endLocation;

	@Schema(description = "途经城市列表")
	private List<String> waypoints;

	@Schema(description = "行程天数", example = "3")
	private Integer days;

	@Schema(description = "规划偏好: auto-自动推荐, spots-只规划景点, relax-休闲模式, intensive-特种兵模式", example = "auto")
	private String preference;

	@Schema(description = "交通方式: driving-自驾, public-公共交通, walking-步行, cycling-骑行", example = "driving")
	private String transport;

}
