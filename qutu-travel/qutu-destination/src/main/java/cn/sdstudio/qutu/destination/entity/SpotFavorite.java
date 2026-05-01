package cn.sdstudio.qutu.destination.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 收藏
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@TableName("t_spot_favorite")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "收藏")
public class SpotFavorite extends Model<SpotFavorite> {

    /**
     * 景点ID
     */
    @Schema(description = "景点ID")
    private Long spotId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 收藏时间
     */
    @Schema(description = "收藏时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
