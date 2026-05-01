package cn.sdstudio.qutu.destination.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 回复VO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "回复VO")
public class ReplyVO {

    @Schema(description = "回复ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "回复内容")
    private String content;

    @Schema(description = "点赞数")
    private Integer likes;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "被回复者用户名")
    private String replyTo;
}
