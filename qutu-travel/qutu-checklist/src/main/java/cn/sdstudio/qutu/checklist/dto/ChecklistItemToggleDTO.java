package cn.sdstudio.qutu.checklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "清单项勾选DTO")
public class ChecklistItemToggleDTO {

    @Schema(description = "是否已勾选：0-未勾，1-已勾")
    private Integer checked;
}
