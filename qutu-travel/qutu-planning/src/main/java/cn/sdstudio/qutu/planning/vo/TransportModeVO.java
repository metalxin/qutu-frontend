package cn.sdstudio.qutu.planning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "交通方式")
public class TransportModeVO {

	@Schema(description = "交通方式ID")
	private String id;

	@Schema(description = "交通方式名称")
	private String name;

	@Schema(description = "图标")
	private String icon;

}
