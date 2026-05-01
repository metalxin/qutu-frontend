package cn.sdstudio.qutu.checklist.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户清单详情VO")
public class ChecklistDetailVO extends ChecklistListVO {

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "更新时间")
    private java.time.LocalDateTime updatedAt;

    @Schema(description = "清单项列表")
    private List<ChecklistItemVO> items = new ArrayList<>();
}
