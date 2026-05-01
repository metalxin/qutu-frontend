package cn.sdstudio.qutu.destination.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 评论点赞
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@TableName("t_comment_like")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "评论点赞")
public class CommentLike extends Model<CommentLike> {

    /**
     * 评论ID
     */
    @Schema(description = "评论ID")
    private Long commentId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
