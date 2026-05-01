package cn.sdstudio.qutu.planning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "路线中的景点")
public class RouteSpotVO {

	@Schema(description = "景点ID")
	private Integer id;

	@Schema(description = "景点名称")
	private String name;

	@Schema(description = "第几天")
	private Integer day;

	@Schema(description = "当天顺序")
	private Integer order;

	@Schema(description = "游玩时长")
	private String duration;

	@Schema(description = "开始时间")
	private String startTime;

	@Schema(description = "结束时间")
	private String endTime;

	@Schema(description = "景点详情")
	private SpotInfoVO spotInfo;

}
