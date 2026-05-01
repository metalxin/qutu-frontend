package cn.sdstudio.qutu.checklist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_checklist_expense")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "清单费用")
public class ChecklistExpense extends Model<ChecklistExpense> {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "清单ID")
    private Long checklistId;

    @Schema(description = "分类：transport|accommodation|food|shopping|entertainment|other")
    private String category;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "消费日期")
    private LocalDate expenseDate;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
