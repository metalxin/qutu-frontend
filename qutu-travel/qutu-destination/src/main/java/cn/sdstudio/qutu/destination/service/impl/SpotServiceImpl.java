package cn.sdstudio.qutu.destination.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.sdstudio.qutu.destination.dto.SpotQueryDTO;
import cn.sdstudio.qutu.destination.entity.Spot;
import cn.sdstudio.qutu.destination.entity.SpotActionLog;
import cn.sdstudio.qutu.destination.entity.SpotFavorite;
import cn.sdstudio.qutu.destination.entity.SpotTip;
import cn.sdstudio.qutu.destination.mapper.*;
import cn.sdstudio.qutu.destination.service.DestinationCityService;
import cn.sdstudio.qutu.destination.service.SpotImageService;
import cn.sdstudio.qutu.destination.service.SpotService;
import cn.sdstudio.qutu.destination.service.SpotTagService;
import cn.sdstudio.qutu.destination.service.SpotTipService;
import cn.sdstudio.qutu.destination.vo.SpotDetailVO;
import cn.sdstudio.qutu.destination.vo.SpotGuideVO;
import cn.sdstudio.qutu.destination.vo.SpotListVO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 景点Service实现
 *
 * @author qutu
 * @date 2026-02-02
 */
@Service
@RequiredArgsConstructor
public class SpotServiceImpl extends ServiceImpl<SpotMapper, Spot> implements SpotService {

    private final SpotFavoriteMapper spotFavoriteMapper;
    private final SpotActionLogMapper spotActionLogMapper;
    private final SpotImageService spotImageService;
    private final SpotTipService spotTipService;
    private final SpotTagService spotTagService;
    private final DestinationCityService destinationCityService;
    private final SpotTagMapMapper spotTagMapMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Spot entity) {
        boolean result = super.save(entity);
        if (result && entity.getCityId() != null) {
            destinationCityService.refreshSpotsCount(entity.getCityId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        Spot spot = this.getById(id);
        boolean result = super.removeById(id);
        if (result && spot != null && spot.getCityId() != null) {
            destinationCityService.refreshSpotsCount(spot.getCityId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Spot entity) {
        Long oldCityId = null;
        Integer oldStatus = null;
        Spot oldSpot = this.getById(entity.getId());
        if (oldSpot != null) {
            oldCityId = oldSpot.getCityId();
            oldStatus = oldSpot.getStatus();
        }
        boolean result = super.updateById(entity);
        if (result) {
            // 状态变更时刷新旧城市和新城市的景点数量
            if (oldCityId != null && (entity.getStatus() != null && !entity.getStatus().equals(oldStatus))) {
                destinationCityService.refreshSpotsCount(oldCityId);
            }
            // 城市变更时刷新两个城市的景点数量
            if (entity.getCityId() != null && !entity.getCityId().equals(oldCityId)) {
                destinationCityService.refreshSpotsCount(entity.getCityId());
                if (oldCityId != null) {
                    destinationCityService.refreshSpotsCount(oldCityId);
                }
            }
        }
        return result;
    }

    @Override
    public IPage<SpotListVO> pageSpots(Page<SpotListVO> page, SpotQueryDTO query) {
        IPage<SpotListVO> resultPage = baseMapper.selectSpotPage(page, query);
        
        // 填充标签
        resultPage.getRecords().forEach(vo -> {
            vo.setTags(spotTagService.getSpotTags(vo.getId()));
        });
        
        return resultPage;
    }

    @Override
    public SpotDetailVO getSpotDetail(Long id, Long userId) {
        SpotDetailVO detail = loadSpotDetail(id);
        if (detail == null) {
            return null;
        }

        // 检查是否收藏
        if (userId != null) {
            detail.setIsFavorite(checkFavorite(id, userId));
        } else {
            detail.setIsFavorite(false);
        }

        // 增加浏览量
        incrementViewCount(id);

        // 记录浏览行为
        if (userId != null) {
            logAction(userId, id, 1);
        }

        return detail;
    }

    @Override
    public SpotDetailVO getSpotEditDetail(Long id) {
        return loadSpotDetail(id);
    }

    @Override
    public List<SpotListVO> getSpotsByCity(Long cityId, int limit) {
        List<Spot> spots = baseMapper.selectByCityId(cityId);
        if (limit > 0 && spots.size() > limit) {
            spots = spots.subList(0, limit);
        }
        return spots.stream().map(this::convertToListVO).collect(Collectors.toList());
    }

    @Override
    public List<SpotListVO> getHotSpots(int limit) {
        List<Spot> spots = baseMapper.selectHotSpots(limit);
        return spots.stream().map(this::convertToListVO).collect(Collectors.toList());
    }

    @Override
    public List<SpotListVO> getNearbySpots(BigDecimal lng, BigDecimal lat, int radius, int limit) {
        return baseMapper.selectNearbySpots(lng, lat, radius, limit);
    }

    @Override
    public List<SpotListVO> searchSpots(String keyword, int limit) {
        List<Spot> spots = baseMapper.fullTextSearch(keyword, limit);
        return spots.stream().map(this::convertToListVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean favoriteSpot(Long spotId, Long userId) {
        // 检查是否已收藏
        if (checkFavorite(spotId, userId)) {
            return true;
        }
        
        // 添加收藏
        SpotFavorite favorite = new SpotFavorite();
        favorite.setSpotId(spotId);
        favorite.setUserId(userId);
        spotFavoriteMapper.insert(favorite);
        
        // 增加收藏数
        baseMapper.incrementFavoriteCount(spotId);
        
        // 记录行为
        logAction(userId, spotId, 2);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfavoriteSpot(Long spotId, Long userId) {
        // 检查是否已收藏
        if (!checkFavorite(spotId, userId)) {
            return true;
        }
        
        // 删除收藏
        spotFavoriteMapper.deleteFavorite(spotId, userId);
        
        // 减少收藏数
        baseMapper.decrementFavoriteCount(spotId);
        
        return true;
    }

    @Override
    public boolean checkFavorite(Long spotId, Long userId) {
        return spotFavoriteMapper.checkFavorite(spotId, userId) > 0;
    }

    @Override
    public IPage<SpotListVO> getUserFavorites(Page<SpotListVO> page, Long userId) {
        return spotFavoriteMapper.selectUserFavorites(page, userId);
    }

    @Override
    public void incrementViewCount(Long id) {
        baseMapper.incrementViewCount(id);
    }

    @Override
    public void logAction(Long userId, Long spotId, int actionType) {
        SpotActionLog log = new SpotActionLog();
        log.setUserId(userId);
        log.setSpotId(spotId);
        log.setActionType(actionType);
        spotActionLogMapper.insert(log);
    }

    @Override
    public List<SpotGuideVO> getRelatedGuides(Long spotId, int limit) {
        return baseMapper.selectRelatedGuides(spotId, limit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSpotGuides(Long spotId, List<Long> guideIds) {
        // 先删除旧的关联
        baseMapper.deleteSpotGuideMaps(spotId);
        // 再插入新的关联
        if (guideIds != null && !guideIds.isEmpty()) {
            baseMapper.insertSpotGuideMaps(spotId, guideIds);
        }
        return true;
    }

    @Override
    public List<Long> getSpotGuideIds(Long spotId) {
        return baseMapper.selectSpotGuideIds(spotId);
    }

    /**
     * 加载景点详情基础数据，不触发浏览量和行为日志。
     */
    private SpotDetailVO loadSpotDetail(Long id) {
        SpotDetailVO detail = baseMapper.selectSpotDetail(id);
        if (detail == null) {
            Spot spot = this.getById(id);
            if (spot == null) {
                return null;
            }
            detail = new SpotDetailVO();
            BeanUtil.copyProperties(spot, detail);
            detail.setCover(spot.getCoverUrl());
            detail.setLocationDetail(spot.getAddress());
            detail.setPrice(spot.getPriceText());
            detail.setReviewCount(spot.getCommentCount());
            detail.setFavoriteCount(spot.getFavoriteCount());
            detail.setViewCount(spot.getViewCount());
        }

        // 填充图片
        detail.setGallery(spotImageService.getSpotImages(id));

        // 填充提示
        detail.setTips(spotTipService.getSpotTips(id));

        // 填充标签
        detail.setTags(spotTagService.getSpotTags(id));

        // 填充关联攻略
        detail.setRelatedGuides(baseMapper.selectRelatedGuides(id, 3));

        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAdminSpot(Long id) {
        Spot spot = this.getById(id);
        if (spot == null) {
            return false;
        }

        // 先清理关联关系，避免主记录删除后遗留脏数据或触发外键约束。
        try {
            baseMapper.deleteSpotGuideMaps(id);
        } catch (Exception e) {
            // 忽略错误，继续执行
        }
        try {
            spotImageService.deleteBySpotId(id);
        } catch (Exception e) {
            // 忽略错误，继续执行
        }
        try {
            spotTipService.remove(Wrappers.<SpotTip>lambdaQuery().eq(SpotTip::getSpotId, id));
        } catch (Exception e) {
            // 忽略错误，继续执行
        }
        try {
            // 直接删除标签关联，而不用 set 方法
            spotTagMapMapper.deleteBySpotId(id);
        } catch (Exception e) {
            // 忽略错误，继续执行
        }
        try {
            spotFavoriteMapper.delete(Wrappers.<SpotFavorite>lambdaQuery().eq(SpotFavorite::getSpotId, id));
        } catch (Exception e) {
            // 忽略错误，继续执行
        }
        try {
            spotActionLogMapper.delete(Wrappers.<SpotActionLog>lambdaQuery().eq(SpotActionLog::getSpotId, id));
        } catch (Exception e) {
            // 忽略错误，继续执行
        }

        int affectedRows = baseMapper.deleteSpotById(id);
        boolean result = affectedRows > 0;
        if (result && spot.getCityId() != null) {
            destinationCityService.refreshSpotsCount(spot.getCityId());
        }
        return result;
    }

    /**
     * 实体转列表VO
     */
    private SpotListVO convertToListVO(Spot spot) {
        SpotListVO vo = new SpotListVO();
        BeanUtil.copyProperties(spot, vo);
        vo.setTags(spotTagService.getSpotTags(spot.getId()));
        return vo;
    }
}
