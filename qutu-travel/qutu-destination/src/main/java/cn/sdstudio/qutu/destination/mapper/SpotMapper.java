package cn.sdstudio.qutu.destination.mapper;

import cn.sdstudio.qutu.destination.dto.SpotQueryDTO;
import cn.sdstudio.qutu.destination.entity.Spot;
import cn.sdstudio.qutu.destination.vo.SpotDetailVO;
import cn.sdstudio.qutu.destination.vo.SpotListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 景点Mapper
 *
 * @author qutu
 * @date 2026-02-02
 */
@Mapper
public interface SpotMapper extends BaseMapper<Spot> {

    /**
     * 分页查询景点列表
     */
    IPage<SpotListVO> selectSpotPage(Page<SpotListVO> page, @Param("query") SpotQueryDTO query);

    /**
     * 获取景点详情
     */
    SpotDetailVO selectSpotDetail(@Param("id") Long id);

    /**
     * 根据城市ID查询景点
     */
    @Select("SELECT * FROM t_spot WHERE city_id = #{cityId} AND status = 1 AND deleted_flag = 0 ORDER BY hot_score DESC")
    List<Spot> selectByCityId(@Param("cityId") Long cityId);

    /**
     * 热门景点
     */
    @Select("SELECT * FROM t_spot WHERE status = 1 AND deleted_flag = 0 ORDER BY hot_score DESC LIMIT #{limit}")
    List<Spot> selectHotSpots(@Param("limit") int limit);

    /**
     * 附近景点（基于距离）
     */
    List<SpotListVO> selectNearbySpots(@Param("lng") BigDecimal lng, @Param("lat") BigDecimal lat, 
                                        @Param("radius") int radius, @Param("limit") int limit);

    /**
     * 增加浏览量
     */
    @Update("UPDATE t_spot SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 增加收藏数
     */
    @Update("UPDATE t_spot SET favorite_count = favorite_count + 1 WHERE id = #{id}")
    void incrementFavoriteCount(@Param("id") Long id);

    /**
     * 减少收藏数
     */
    @Update("UPDATE t_spot SET favorite_count = GREATEST(favorite_count - 1, 0) WHERE id = #{id}")
    void decrementFavoriteCount(@Param("id") Long id);

    /**
     * 增加评论数
     */
    @Update("UPDATE t_spot SET comment_count = comment_count + 1 WHERE id = #{id}")
    void incrementCommentCount(@Param("id") Long id);

    /**
     * 全文搜索
     */
    @Select("SELECT * FROM t_spot WHERE MATCH(name, description) AGAINST(#{keyword} IN NATURAL LANGUAGE MODE) AND status = 1 AND deleted_flag = 0 LIMIT #{limit}")
    List<Spot> fullTextSearch(@Param("keyword") String keyword, @Param("limit") int limit);

    /**
     * 获取景点标签
     */
    @Select("SELECT t.name FROM t_spot_tag t INNER JOIN t_spot_tag_map m ON t.id = m.tag_id WHERE m.spot_id = #{spotId}")
    List<String> selectSpotTags(@Param("spotId") Long spotId);

    /**
     * 获取景点关联攻略
     */
    List<cn.sdstudio.qutu.destination.vo.SpotGuideVO> selectRelatedGuides(@Param("spotId") Long spotId, @Param("limit") int limit);

    /**
     * 删除景点关联攻略
     */
    @Delete("DELETE FROM t_guide_spot_map WHERE spot_id = #{spotId}")
    void deleteSpotGuideMaps(@Param("spotId") Long spotId);

    /**
     * 插入景点关联攻略
     */
    void insertSpotGuideMaps(@Param("spotId") Long spotId, @Param("guideIds") List<Long> guideIds);

    /**
     * 获取景点关联的所有攻略ID
     */
    @Select("SELECT guide_id FROM t_guide_spot_map WHERE spot_id = #{spotId}")
    List<Long> selectSpotGuideIds(@Param("spotId") Long spotId);

    /**
     * 物理删除景点主记录
     */
    @Delete("DELETE FROM t_spot WHERE id = #{id}")
    int deleteSpotById(@Param("id") Long id);
}
