package cn.sdstudio.qutu.destination.mapper;

import cn.sdstudio.qutu.destination.entity.CommentLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 评论点赞Mapper
 *
 * @author qutu
 * @date 2026-02-02
 */
@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLike> {

    /**
     * 检查用户是否点赞
     */
    @Select("SELECT COUNT(1) FROM t_comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    int checkLiked(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 删除点赞
     */
    @Delete("DELETE FROM t_comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    void deleteLike(@Param("commentId") Long commentId, @Param("userId") Long userId);
}
