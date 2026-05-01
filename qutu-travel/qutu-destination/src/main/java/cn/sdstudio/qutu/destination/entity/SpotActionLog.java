package cn.sdstudio.qutu.destination.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 行为日志
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@TableName("t_spot_action_log")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "行为日志")
public class SpotActionLog extends Model<SpotActionLog> {

    /**
     * 日志ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "日志ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 景点ID
     */
    @Schema(description = "景点ID")
    private Long spotId;

    /**
     * 行为类型
     */
    @Schema(description = "行为类型：1-浏览，2-收藏，3-评论，4-分享")
    private Integer actionType;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
