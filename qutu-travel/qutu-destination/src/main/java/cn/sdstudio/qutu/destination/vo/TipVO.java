package cn.sdstudio.qutu.destination.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 提示信息VO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "提示信息VO")
public class TipVO {

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String desc;

    @Schema(description = "排序")
    private Integer orderIndex;
}
