package cn.sdstudio.qutu.destination.mapper;

import cn.sdstudio.qutu.destination.entity.SpotTagMap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;

/**
 * 标签关联Mapper
 *
 * @author qutu
 * @date 2026-02-02
 */
@Mapper
public interface SpotTagMapMapper extends BaseMapper<SpotTagMap> {

    /**
     * 删除景点的所有标签关联
     */
    @Delete("DELETE FROM t_spot_tag_map WHERE spot_id = #{spotId}")
    void deleteBySpotId(@Param("spotId") Long spotId);
}
