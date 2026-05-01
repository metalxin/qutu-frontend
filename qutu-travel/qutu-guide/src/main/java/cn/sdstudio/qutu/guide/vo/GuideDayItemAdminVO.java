package cn.sdstudio.qutu.guide.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "攻略后台日程条目VO")
public class GuideDayItemAdminVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "类型：text|spot")
    private String type;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "关联景点ID")
    private Long spotId;

    @Schema(description = "排序")
    private Integer orderNum;
}
