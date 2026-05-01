package cn.sdstudio.qutu.checklist.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "清单后台详情VO")
public class ChecklistAdminDetailVO extends ChecklistAdminVO {

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "更新时间")
    private java.time.LocalDateTime updatedAt;

    @Schema(description = "清单项列表")
    private List<ChecklistItemAdminVO> items = new ArrayList<>();
}
