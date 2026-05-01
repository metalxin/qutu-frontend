package cn.sdstudio.qutu.destination.mapper;

import cn.sdstudio.qutu.destination.entity.SpotTip;
import cn.sdstudio.qutu.destination.vo.TipVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 景点提示Mapper
 *
 * @author qutu
 * @date 2026-02-02
 */
@Mapper
public interface SpotTipMapper extends BaseMapper<SpotTip> {

    /**
     * 根据景点ID获取提示列表
     */
    @Select("SELECT icon, title, description as `desc`, order_index as orderIndex FROM t_spot_tip WHERE spot_id = #{spotId} ORDER BY order_index")
    List<TipVO> selectBySpotId(@Param("spotId") Long spotId);
}
