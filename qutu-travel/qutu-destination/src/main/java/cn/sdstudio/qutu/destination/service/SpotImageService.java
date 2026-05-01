package cn.sdstudio.qutu.destination.service;

import cn.sdstudio.qutu.destination.entity.SpotImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 景点图片Service
 *
 * @author qutu
 * @date 2026-02-02
 */
public interface SpotImageService extends IService<SpotImage> {

    /**
     * 获取景点图片列表
     */
    List<String> getSpotImages(Long spotId);

    /**
     * 批量保存图片
     */
    boolean saveImages(Long spotId, List<String> urls);

    /**
     * 删除景点所有图片
     */
    boolean deleteBySpotId(Long spotId);
}
