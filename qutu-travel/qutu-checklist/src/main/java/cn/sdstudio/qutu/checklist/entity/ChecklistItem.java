package cn.sdstudio.qutu.checklist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("t_checklist_item")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "清单项")
public class ChecklistItem extends Model<ChecklistItem> {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "清单ID")
    private Long checklistId;

    @Schema(description = "分类：prepare|document|clothing|daily|food|medicine|electronics|other")
    private String category;

    @Schema(description = "项目内容")
    private String content;

    @Schema(description = "是否已勾选：0-未勾，1-已勾")
    private Integer checked;

    @Schema(description = "排序")
    private Integer orderNum;
}
