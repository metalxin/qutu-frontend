package cn.sdstudio.qutu.destination.mapper;

import cn.sdstudio.qutu.destination.entity.SpotImage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 景点图片Mapper
 *
 * @author qutu
 * @date 2026-02-02
 */
@Mapper
public interface SpotImageMapper extends BaseMapper<SpotImage> {

    /**
     * 根据景点ID获取图片列表
     */
    @Select("SELECT url FROM t_spot_image WHERE spot_id = #{spotId} ORDER BY order_index")
    List<String> selectUrlsBySpotId(@Param("spotId") Long spotId);
}
