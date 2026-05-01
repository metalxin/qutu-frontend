package cn.sdstudio.qutu.destination.service.impl;

import cn.sdstudio.qutu.destination.entity.SpotImage;
import cn.sdstudio.qutu.destination.mapper.SpotImageMapper;
import cn.sdstudio.qutu.destination.service.SpotImageService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 景点图片Service实现
 *
 * @author qutu
 * @date 2026-02-02
 */
@Service
@RequiredArgsConstructor
public class SpotImageServiceImpl extends ServiceImpl<SpotImageMapper, SpotImage> implements SpotImageService {

    @Override
    public List<String> getSpotImages(Long spotId) {
        return baseMapper.selectUrlsBySpotId(spotId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveImages(Long spotId, List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return true;
        }
        
        List<SpotImage> images = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            SpotImage image = new SpotImage();
            image.setSpotId(spotId);
            image.setUrl(urls.get(i));
            image.setOrderIndex(i);
            images.add(image);
        }
        
        return this.saveBatch(images);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBySpotId(Long spotId) {
        return this.remove(Wrappers.<SpotImage>lambdaQuery().eq(SpotImage::getSpotId, spotId));
    }
}
