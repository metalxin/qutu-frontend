package cn.sdstudio.qutu.checklist.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "清单项后台VO")
public class ChecklistItemAdminVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "项目内容")
    private String content;

    @Schema(description = "是否已勾选")
    private Integer checked;

    @Schema(description = "排序")
    private Integer orderNum;
}
