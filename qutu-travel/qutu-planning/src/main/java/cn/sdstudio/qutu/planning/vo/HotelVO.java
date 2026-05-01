package cn.sdstudio.qutu.planning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "住宿推荐")
public class HotelVO {

	@Schema(description = "酒店名称")
	private String name;

	@Schema(description = "价格")
	private String price;

}
