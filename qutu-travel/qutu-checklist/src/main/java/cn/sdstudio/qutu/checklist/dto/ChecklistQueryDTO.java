package cn.sdstudio.qutu.checklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "清单查询DTO")
public class ChecklistQueryDTO {

    @Schema(description = "关键词搜索（标题/目的地）")
    private String keyword;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "状态：0-未开始，1-进行中，2-已完成")
    private Integer status;
}
