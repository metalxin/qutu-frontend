package cn.sdstudio.qutu.checklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "清单后台DTO")
public class ChecklistAdminDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "清单标题")
    private String title;

    @Schema(description = "目的地")
    private String destination;

    @Schema(description = "出发日期")
    private LocalDate departDate;

    @Schema(description = "返回日期")
    private LocalDate returnDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：0-未开始，1-进行中，2-已完成")
    private Integer status;

    @Schema(description = "清单项列表")
    private List<ChecklistItemDTO> items = new ArrayList<>();
}
