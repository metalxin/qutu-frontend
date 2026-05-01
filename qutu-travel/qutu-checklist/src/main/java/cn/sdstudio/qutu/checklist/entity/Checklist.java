package cn.sdstudio.qutu.checklist.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("t_checklist")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "行前清单")
public class Checklist extends Model<Checklist> {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "清单标题")
    private String title;

    @Schema(description = "目的地")
    private String destination;

    @Schema(description = "出发日期")
    private java.time.LocalDate departDate;

    @Schema(description = "返回日期")
    private java.time.LocalDate returnDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：0-未开始，1-进行中，2-已完成")
    private Integer status;

    @Schema(description = "清单项数量")
    private Integer itemsCount;

    @Schema(description = "已完成项数量")
    private Integer checkedCount;

    @Schema(description = "预算金额")
    private java.math.BigDecimal budget;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deletedFlag;
}
