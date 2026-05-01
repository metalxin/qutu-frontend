package cn.sdstudio.qutu.destination.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.security.util.SecurityUtils;
import cn.sdstudio.qutu.destination.dto.CommentDTO;
import cn.sdstudio.qutu.destination.dto.ReplyDTO;
import cn.sdstudio.qutu.destination.service.SpotCommentService;
import cn.sdstudio.qutu.destination.vo.CommentVO;
import cn.sdstudio.qutu.destination.vo.ReplyVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * 评论Controller
 *
 * @author qutu
 * @date 2026-02-02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "评论管理", description = "景点评论相关接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class CommentController {

    private final SpotCommentService spotCommentService;

    /**
     * 获取景点评论
     */
    @Operation(summary = "获取景点评论", description = "分页获取景点的评论列表")
    @GetMapping("/spots/{spotId}/comments")
    public R<?> getSpotComments(
            @Parameter(description = "景点ID") @PathVariable Long spotId,
            @ParameterObject Page<CommentVO> page) {
        Long userId = null;
        try {
            userId = SecurityUtils.getUser().getId();
        } catch (Exception ignored) {
        }
        return R.ok(spotCommentService.pageComments(page, spotId, userId));
    }

    /**
     * 发表评论
     */
    @Operation(summary = "发表评论", description = "对景点发表评论")
    @PostMapping("/spots/{spotId}/comments")
    public R<CommentVO> postComment(
            @Parameter(description = "景点ID") @PathVariable Long spotId,
            @Valid @RequestBody CommentDTO dto) {
        dto.setSpotId(spotId);
        var user = SecurityUtils.getUser();
        return R.ok(spotCommentService.postComment(dto, user.getId(), user.getUsername(), 
                user.getAttributes() != null ? (String) user.getAttributes().get("avatar") : null));
    }

    /**
     * 回复评论
     */
    @Operation(summary = "回复评论", description = "回复指定评论")
    @PostMapping("/comments/{commentId}/replies")
    public R<ReplyVO> replyComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            @Valid @RequestBody ReplyDTO dto) {
        dto.setParentId(commentId);
        var user = SecurityUtils.getUser();
        return R.ok(spotCommentService.replyComment(dto, user.getId(), user.getUsername(),
                user.getAttributes() != null ? (String) user.getAttributes().get("avatar") : null));
    }

    /**
     * 点赞评论
     */
    @Operation(summary = "点赞评论", description = "对评论进行点赞")
    @PostMapping("/comments/{commentId}/like")
    public R<Boolean> likeComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId) {
        Long userId = SecurityUtils.getUser().getId();
        return R.ok(spotCommentService.likeComment(commentId, userId));
    }

    /**
     * 取消点赞
     */
    @Operation(summary = "取消点赞", description = "取消对评论的点赞")
    @DeleteMapping("/comments/{commentId}/like")
    public R<Boolean> unlikeComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId) {
        Long userId = SecurityUtils.getUser().getId();
        return R.ok(spotCommentService.unlikeComment(commentId, userId));
    }

    /**
     * 删除评论
     */
    @Operation(summary = "删除评论", description = "删除自己发表的评论")
    @DeleteMapping("/comments/{commentId}")
    public R<Boolean> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId) {
        Long userId = SecurityUtils.getUser().getId();
        return R.ok(spotCommentService.deleteComment(commentId, userId));
    }
}
