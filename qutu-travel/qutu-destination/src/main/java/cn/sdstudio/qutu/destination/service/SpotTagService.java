package cn.sdstudio.qutu.destination.service;

import cn.sdstudio.qutu.destination.entity.SpotTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 标签Service
 *
 * @author qutu
 * @date 2026-02-02
 */
public interface SpotTagService extends IService<SpotTag> {

    /**
     * 获取景点标签
     */
    List<String> getSpotTags(Long spotId);

    /**
     * 设置景点标签
     */
    boolean setSpotTags(Long spotId, List<Long> tagIds);

    /**
     * 获取热门标签
     */
    List<SpotTag> getHotTags(int limit);
}
