package cn.sdstudio.qutu.destination.mapper;

import cn.sdstudio.qutu.destination.entity.DestinationCity;
import cn.sdstudio.qutu.destination.vo.DestinationVO;
import cn.sdstudio.qutu.destination.vo.RegionGroupVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 目的地城市Mapper
 *
 * @author qutu
 * @date 2026-02-02
 */
@Mapper
public interface DestinationCityMapper extends BaseMapper<DestinationCity> {

    /**
     * 获取热门城市列表
     */
    @Select("SELECT name FROM t_destination_city WHERE is_hot = 1 AND deleted_flag = 0 ORDER BY hot_score DESC LIMIT #{limit}")
    List<String> selectHotCities(@Param("limit") int limit);

    /**
     * 根据省份分组获取城市
     */
    @Select("SELECT DISTINCT province FROM t_destination_city WHERE deleted_flag = 0 ORDER BY province")
    List<String> selectAllProvinces();

    /**
     * 根据省份获取城市列表
     */
    @Select("SELECT * FROM t_destination_city WHERE province = #{province} AND deleted_flag = 0")
    List<DestinationCity> selectByProvince(@Param("province") String province);

    /**
     * 增加浏览量
     */
    @Update("UPDATE t_destination_city SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 根据城市ID重新统计景点数量并更新spots_count字段
     */
    @Update("UPDATE t_destination_city SET spots_count = (SELECT COUNT(*) FROM t_spot WHERE city_id = #{cityId} AND status = 1 AND deleted_flag = 0) WHERE id = #{cityId}")
    void refreshSpotsCount(@Param("cityId") Long cityId);
}
