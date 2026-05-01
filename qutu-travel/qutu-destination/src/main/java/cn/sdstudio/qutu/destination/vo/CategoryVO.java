package cn.sdstudio.qutu.destination.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分类VO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "分类VO")
public class CategoryVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "子分类")
    private List<CategoryVO> children;
}
