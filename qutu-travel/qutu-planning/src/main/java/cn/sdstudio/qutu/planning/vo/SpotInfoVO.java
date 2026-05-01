package cn.sdstudio.qutu.planning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "景点信息")
public class SpotInfoVO {

	@Schema(description = "景点ID")
	private Integer id;

	@Schema(description = "景点名称")
	private String name;

	@Schema(description = "评分")
	private Double rating;

	@Schema(description = "建议游玩时长")
	private String duration;

	@Schema(description = "门票价格")
	private String price;

	@Schema(description = "景点分类")
	private String category;

	@Schema(description = "景点描述")
	private String description;

	@Schema(description = "游玩贴士")
	private String tips;

	@Schema(description = "开放时间")
	private String openTime;

	@Schema(description = "地址")
	private String address;

	@Schema(description = "纬度")
	private Double latitude;

	@Schema(description = "经度")
	private Double longitude;

}
