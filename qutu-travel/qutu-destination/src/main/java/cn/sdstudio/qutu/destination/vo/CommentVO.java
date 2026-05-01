package cn.sdstudio.qutu.destination.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 评论VO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "评论VO")
public class CommentVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "图片列表")
    private List<String> images;

    @Schema(description = "点赞数")
    private Integer likes;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "回复列表")
    private List<ReplyVO> replies;

    @Schema(description = "是否显示全部回复")
    private Boolean showAllReplies;

    @Schema(description = "回复总数")
    private Integer replyCount;
}
