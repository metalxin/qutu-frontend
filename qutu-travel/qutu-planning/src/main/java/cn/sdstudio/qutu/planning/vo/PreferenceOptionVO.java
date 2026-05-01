package cn.sdstudio.qutu.planning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "规划偏好选项")
public class PreferenceOptionVO {

	@Schema(description = "偏好ID")
	private String id;

	@Schema(description = "偏好名称")
	private String name;

	@Schema(description = "偏好描述")
	private String desc;

}
