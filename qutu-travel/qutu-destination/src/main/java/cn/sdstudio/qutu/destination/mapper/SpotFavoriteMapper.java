package cn.sdstudio.qutu.destination.mapper;

import cn.sdstudio.qutu.destination.entity.SpotFavorite;
import cn.sdstudio.qutu.destination.vo.SpotListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 收藏Mapper
 *
 * @author qutu
 * @date 2026-02-02
 */
@Mapper
public interface SpotFavoriteMapper extends BaseMapper<SpotFavorite> {

    /**
     * 检查用户是否收藏
     */
    @Select("SELECT COUNT(1) FROM t_spot_favorite WHERE spot_id = #{spotId} AND user_id = #{userId}")
    int checkFavorite(@Param("spotId") Long spotId, @Param("userId") Long userId);

    /**
     * 删除收藏
     */
    @Delete("DELETE FROM t_spot_favorite WHERE spot_id = #{spotId} AND user_id = #{userId}")
    void deleteFavorite(@Param("spotId") Long spotId, @Param("userId") Long userId);

    /**
     * 分页查询用户收藏的景点
     */
    IPage<SpotListVO> selectUserFavorites(Page<SpotListVO> page, @Param("userId") Long userId);
}
