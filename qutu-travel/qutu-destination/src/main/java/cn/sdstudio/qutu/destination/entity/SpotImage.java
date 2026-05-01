package cn.sdstudio.qutu.destination.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 景点图片
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@TableName("t_spot_image")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "景点图片")
public class SpotImage extends Model<SpotImage> {

    /**
     * 图片ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "图片ID")
    private Long id;

    /**
     * 景点ID
     */
    @Schema(description = "景点ID")
    private Long spotId;

    /**
     * 图片URL
     */
    @Schema(description = "图片URL")
    private String url;

    /**
     * 宽度
     */
    @Schema(description = "宽度")
    private Integer width;

    /**
     * 高度
     */
    @Schema(description = "高度")
    private Integer height;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer orderIndex;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
