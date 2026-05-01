package cn.sdstudio.qutu.destination.mapper;

import cn.sdstudio.qutu.destination.entity.SpotCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 景点分类Mapper
 *
 * @author qutu
 * @date 2026-02-02
 */
@Mapper
public interface SpotCategoryMapper extends BaseMapper<SpotCategory> {

    /**
     * 获取子分类
     */
    @Select("SELECT * FROM t_spot_category WHERE parent_id = #{parentId} ORDER BY sort_order")
    List<SpotCategory> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 获取顶级分类
     */
    @Select("SELECT * FROM t_spot_category WHERE parent_id IS NULL OR parent_id = 0 ORDER BY sort_order")
    List<SpotCategory> selectRootCategories();
}
