package cn.sdstudio.qutu.destination.mapper;

import cn.sdstudio.qutu.destination.entity.SpotComment;
import cn.sdstudio.qutu.destination.vo.CommentVO;
import cn.sdstudio.qutu.destination.vo.ReplyVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 评论Mapper
 *
 * @author qutu
 * @date 2026-02-02
 */
@Mapper
public interface SpotCommentMapper extends BaseMapper<SpotComment> {

    /**
     * 分页查询景点评论
     */
    IPage<CommentVO> selectCommentPage(Page<CommentVO> page, @Param("spotId") Long spotId, @Param("userId") Long userId);

    /**
     * 查询评论的回复列表
     */
    List<ReplyVO> selectReplies(@Param("parentId") Long parentId, @Param("userId") Long userId);

    /**
     * 增加点赞数
     */
    @Update("UPDATE t_spot_comment SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Long id);

    /**
     * 减少点赞数
     */
    @Update("UPDATE t_spot_comment SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}")
    void decrementLikeCount(@Param("id") Long id);

    /**
     * 增加回复数
     */
    @Update("UPDATE t_spot_comment SET reply_count = reply_count + 1 WHERE id = #{id}")
    void incrementReplyCount(@Param("id") Long id);

    /**
     * 查询用户对景点的评论
     */
    @Select("SELECT * FROM t_spot_comment WHERE spot_id = #{spotId} AND user_id = #{userId} AND parent_id IS NULL")
    SpotComment selectBySpotAndUser(@Param("spotId") Long spotId, @Param("userId") Long userId);
}
