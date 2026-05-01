package cn.sdstudio.qutu.checklist.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户清单项VO")
public class ChecklistItemVO {

    @Schema(description = "主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "项目内容")
    private String content;

    @Schema(description = "是否已勾选：0-未勾，1-已勾")
    private Integer checked;

    @Schema(description = "排序")
    private Integer orderNum;
}
