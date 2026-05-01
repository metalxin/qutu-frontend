package cn.sdstudio.qutu.checklist.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "清单后台列表VO")
public class ChecklistAdminVO {

    @Schema(description = "清单ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "目的地")
    private String destination;

    @Schema(description = "出发日期")
    private LocalDate departDate;

    @Schema(description = "返回日期")
    private LocalDate returnDate;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "清单项数量")
    private Integer itemsCount;

    @Schema(description = "已完成项数量")
    private Integer checkedCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
