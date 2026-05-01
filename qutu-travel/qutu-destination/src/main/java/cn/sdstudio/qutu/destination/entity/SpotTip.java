package cn.sdstudio.qutu.destination.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点提示
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@TableName("t_spot_tip")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "景点提示")
public class SpotTip extends Model<SpotTip> {

    /**
     * 提示ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "提示ID")
    private Long id;

    /**
     * 景点ID
     */
    @Schema(description = "景点ID")
    private Long spotId;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;

    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;

    /**
     * 说明
     */
    @Schema(description = "说明")
    private String description;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer orderIndex;
}
