package cn.sdstudio.qutu.checklist.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户清单列表VO")
public class ChecklistListVO {

    @Schema(description = "清单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "目的地")
    private String destination;

    @Schema(description = "出发日期")
    private LocalDate departDate;

    @Schema(description = "返回日期")
    private LocalDate returnDate;

    @Schema(description = "状态：0-未开始，1-进行中，2-已完成")
    private Integer status;

    @Schema(description = "清单项数量")
    private Integer itemsCount;

    @Schema(description = "已完成项数量")
    private Integer checkedCount;

    @Schema(description = "预算金额")
    private java.math.BigDecimal budget;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
