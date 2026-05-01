package cn.sdstudio.qutu.destination.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 景点分类
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@TableName("t_spot_category")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "景点分类")
public class SpotCategory extends Model<SpotCategory> {

    /**
     * 分类ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "分类ID")
    private Long id;

    /**
     * 父分类ID
     */
    @Schema(description = "父分类ID")
    private Long parentId;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String name;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
