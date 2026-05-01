package cn.sdstudio.qutu.guide.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "攻略标签VO")
public class GuideTagVO {

    @Schema(description = "标签名")
    private String name;

    @Schema(description = "使用次数")
    private Integer count;
}
