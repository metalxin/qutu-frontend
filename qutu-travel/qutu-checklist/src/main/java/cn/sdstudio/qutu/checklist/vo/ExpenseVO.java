package cn.sdstudio.qutu.checklist.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "费用VO")
public class ExpenseVO {

    @Schema(description = "主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "清单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long checklistId;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "消费日期")
    private String expenseDate;
}
