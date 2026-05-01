package cn.sdstudio.qutu.destination.service;

import cn.sdstudio.qutu.destination.dto.SpotQueryDTO;
import cn.sdstudio.qutu.destination.entity.Spot;
import cn.sdstudio.qutu.destination.vo.SpotDetailVO;
import cn.sdstudio.qutu.destination.vo.SpotListVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 景点Service
 *
 * @author qutu
 * @date 2026-02-02
 */
public interface SpotService extends IService<Spot> {

    /**
     * 分页查询景点
     */
    IPage<SpotListVO> pageSpots(Page<SpotListVO> page, SpotQueryDTO query);

    /**
     * 获取景点详情
     */
    SpotDetailVO getSpotDetail(Long id, Long userId);

    /**
     * 获取景点编辑回显详情
     */
    SpotDetailVO getSpotEditDetail(Long id);

    /**
     * 根据城市ID查询景点
     */
    List<SpotListVO> getSpotsByCity(Long cityId, int limit);

    /**
     * 获取热门景点
     */
    List<SpotListVO> getHotSpots(int limit);

    /**
     * 获取附近景点
     */
    List<SpotListVO> getNearbySpots(BigDecimal lng, BigDecimal lat, int radius, int limit);

    /**
     * 搜索景点
     */
    List<SpotListVO> searchSpots(String keyword, int limit);

    /**
     * 收藏景点
     */
    boolean favoriteSpot(Long spotId, Long userId);

    /**
     * 取消收藏景点
     */
    boolean unfavoriteSpot(Long spotId, Long userId);

    /**
     * 检查是否收藏
     */
    boolean checkFavorite(Long spotId, Long userId);

    /**
     * 获取用户收藏的景点
     */
    IPage<SpotListVO> getUserFavorites(Page<SpotListVO> page, Long userId);

    /**
     * 增加浏览量
     */
    void incrementViewCount(Long id);

    /**
     * 记录用户行为
     */
    void logAction(Long userId, Long spotId, int actionType);

    /**
     * 获取景点关联攻略
     */
    List<cn.sdstudio.qutu.destination.vo.SpotGuideVO> getRelatedGuides(Long spotId, int limit);

    /**
     * 更新景点关联攻略
     */
    boolean updateSpotGuides(Long spotId, List<Long> guideIds);

    /**
     * 获取景点关联攻略ID列表
     */
    List<Long> getSpotGuideIds(Long spotId);

    /**
     * 管理端删除景点（包含关联数据清理）
     */
    boolean removeAdminSpot(Long id);
}
