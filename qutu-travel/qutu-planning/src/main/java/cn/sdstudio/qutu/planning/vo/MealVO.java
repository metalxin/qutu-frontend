package cn.sdstudio.qutu.planning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "餐饮推荐")
public class MealVO {

	@Schema(description = "餐厅名称")
	private String name;

	@Schema(description = "餐厅地址")
	private String address;

}
