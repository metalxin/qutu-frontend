package cn.sdstudio.qutu.checklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "清单项DTO")
public class ChecklistItemDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "分类：prepare|document|clothing|daily|food|medicine|electronics|other")
    private String category;

    @Schema(description = "项目内容")
    private String content;

    @Schema(description = "是否已勾选：0-未勾，1-已勾")
    private Integer checked;

    @Schema(description = "排序")
    private Integer orderNum;
}
