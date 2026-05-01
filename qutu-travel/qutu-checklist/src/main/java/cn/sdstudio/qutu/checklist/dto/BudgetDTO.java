package cn.sdstudio.qutu.checklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "预算DTO")
public class BudgetDTO {

    @Schema(description = "预算金额")
    private BigDecimal budget;
}
