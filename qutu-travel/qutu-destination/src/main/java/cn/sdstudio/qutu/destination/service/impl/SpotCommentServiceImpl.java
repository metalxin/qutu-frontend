package cn.sdstudio.qutu.destination.service.impl;

import cn.sdstudio.qutu.destination.dto.CommentDTO;
import cn.sdstudio.qutu.destination.dto.ReplyDTO;
import cn.sdstudio.qutu.destination.entity.CommentLike;
import cn.sdstudio.qutu.destination.entity.SpotComment;
import cn.sdstudio.qutu.destination.mapper.CommentLikeMapper;
import cn.sdstudio.qutu.destination.mapper.SpotCommentMapper;
import cn.sdstudio.qutu.destination.mapper.SpotMapper;
import cn.sdstudio.qutu.destination.service.SpotCommentService;
import cn.sdstudio.qutu.destination.vo.CommentVO;
import cn.sdstudio.qutu.destination.vo.ReplyVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论Service实现
 *
 * @author qutu
 * @date 2026-02-02
 */
@Service
@RequiredArgsConstructor
public class SpotCommentServiceImpl extends ServiceImpl<SpotCommentMapper, SpotComment> 
        implements SpotCommentService {

    private final CommentLikeMapper commentLikeMapper;
    private final SpotMapper spotMapper;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public IPage<CommentVO> pageComments(Page<CommentVO> page, Long spotId, Long userId) {
        IPage<CommentVO> resultPage = baseMapper.selectCommentPage(page, spotId, userId);
        
        // 填充回复
        resultPage.getRecords().forEach(comment -> {
            List<ReplyVO> replies = baseMapper.selectReplies(comment.getId(), userId);
            comment.setReplies(replies);
            comment.setReplyCount(replies.size());
            comment.setShowAllReplies(false);
            
            // 如果没有图片，设置空列表
            if (comment.getImages() == null) {
                comment.setImages(new ArrayList<>());
            }
        });
        
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO postComment(CommentDTO dto, Long userId, String userName, String userAvatar) {
        // 创建评论
        SpotComment comment = new SpotComment();
        comment.setSpotId(dto.getSpotId());
        comment.setUserId(userId);
        comment.setUserName(userName);
        comment.setUserAvatar(userAvatar);
        comment.setContent(dto.getContent());
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        this.save(comment);
        
        // 增加景点评论数
        spotMapper.incrementCommentCount(dto.getSpotId());
        
        // 返回VO
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setUserId(userId);
        vo.setUserName(userName);
        vo.setUserAvatar(userAvatar);
        vo.setContent(dto.getContent());
        vo.setImages(dto.getImages() != null ? dto.getImages() : new ArrayList<>());
        vo.setLikes(0);
        vo.setIsLiked(false);
        vo.setCreateTime(LocalDateTime.now().format(FORMATTER));
        vo.setReplies(new ArrayList<>());
        vo.setShowAllReplies(false);
        vo.setReplyCount(0);
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReplyVO replyComment(ReplyDTO dto, Long userId, String userName, String userAvatar) {
        // 创建回复
        SpotComment reply = new SpotComment();
        reply.setParentId(dto.getParentId());
        reply.setUserId(userId);
        reply.setUserName(userName);
        reply.setUserAvatar(userAvatar);
        reply.setContent(dto.getContent());
        reply.setLikeCount(0);
        reply.setReplyCount(0);
        
        // 获取父评论的spotId
        SpotComment parent = this.getById(dto.getParentId());
        if (parent != null) {
            reply.setSpotId(parent.getSpotId());
        }
        
        this.save(reply);
        
        // 增加父评论回复数
        baseMapper.incrementReplyCount(dto.getParentId());
        
        // 返回VO
        ReplyVO vo = new ReplyVO();
        vo.setId(reply.getId());
        vo.setUserId(userId);
        vo.setUserName(userName);
        vo.setUserAvatar(userAvatar);
        vo.setContent(dto.getContent());
        vo.setLikes(0);
        vo.setIsLiked(false);
        vo.setCreateTime(LocalDateTime.now().format(FORMATTER));
        vo.setReplyTo(dto.getReplyTo());
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeComment(Long commentId, Long userId) {
        // 检查是否已点赞
        if (checkLiked(commentId, userId)) {
            return true;
        }
        
        // 添加点赞
        CommentLike like = new CommentLike();
        like.setCommentId(commentId);
        like.setUserId(userId);
        commentLikeMapper.insert(like);
        
        // 增加点赞数
        baseMapper.incrementLikeCount(commentId);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlikeComment(Long commentId, Long userId) {
        // 检查是否已点赞
        if (!checkLiked(commentId, userId)) {
            return true;
        }
        
        // 删除点赞
        commentLikeMapper.deleteLike(commentId, userId);
        
        // 减少点赞数
        baseMapper.decrementLikeCount(commentId);
        
        return true;
    }

    @Override
    public boolean checkLiked(Long commentId, Long userId) {
        return commentLikeMapper.checkLiked(commentId, userId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId, Long userId) {
        SpotComment comment = this.getById(commentId);
        if (comment == null) {
            return false;
        }
        
        // 只能删除自己的评论
        if (!comment.getUserId().equals(userId)) {
            return false;
        }
        
        return this.removeById(commentId);
    }
}
