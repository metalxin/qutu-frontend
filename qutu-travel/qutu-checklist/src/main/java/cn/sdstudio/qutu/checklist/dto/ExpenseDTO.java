package cn.sdstudio.qutu.checklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "费用DTO")
public class ExpenseDTO {

    @Schema(description = "分类：transport|accommodation|food|shopping|entertainment|other")
    private String category;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "消费日期（yyyy-MM-dd）")
    private String expenseDate;
}
