package cn.sdstudio.qutu.destination.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 评论
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@TableName("t_spot_comment")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "评论")
public class SpotComment extends Model<SpotComment> {

    /**
     * 评论ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "评论ID")
    private Long id;

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
     * 用户名
     */
    @Schema(description = "用户名")
    private String userName;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String userAvatar;

    /**
     * 评论内容
     */
    @Schema(description = "评论内容")
    private String content;

    /**
     * 父评论ID
     */
    @Schema(description = "父评论ID，用于回复功能")
    private Long parentId;

    /**
     * 点赞数
     */
    @Schema(description = "点赞数")
    private Integer likeCount;

    /**
     * 回复数
     */
    @Schema(description = "回复数")
    private Integer replyCount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
