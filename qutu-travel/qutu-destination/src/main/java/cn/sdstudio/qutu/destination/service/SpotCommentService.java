package cn.sdstudio.qutu.destination.service;

import cn.sdstudio.qutu.destination.dto.CommentDTO;
import cn.sdstudio.qutu.destination.dto.ReplyDTO;
import cn.sdstudio.qutu.destination.entity.SpotComment;
import cn.sdstudio.qutu.destination.vo.CommentVO;
import cn.sdstudio.qutu.destination.vo.ReplyVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 评论Service
 *
 * @author qutu
 * @date 2026-02-02
 */
public interface SpotCommentService extends IService<SpotComment> {

    /**
     * 分页查询景点评论
     */
    IPage<CommentVO> pageComments(Page<CommentVO> page, Long spotId, Long userId);

    /**
     * 发表评论
     */
    CommentVO postComment(CommentDTO dto, Long userId, String userName, String userAvatar);

    /**
     * 回复评论
     */
    ReplyVO replyComment(ReplyDTO dto, Long userId, String userName, String userAvatar);

    /**
     * 点赞评论
     */
    boolean likeComment(Long commentId, Long userId);

    /**
     * 取消点赞
     */
    boolean unlikeComment(Long commentId, Long userId);

    /**
     * 检查是否点赞
     */
    boolean checkLiked(Long commentId, Long userId);

    /**
     * 删除评论
     */
    boolean deleteComment(Long commentId, Long userId);
}
