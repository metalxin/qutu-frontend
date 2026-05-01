package cn.sdstudio.qutu.destination.service.impl;

import cn.sdstudio.qutu.destination.entity.SpotTag;
import cn.sdstudio.qutu.destination.entity.SpotTagMap;
import cn.sdstudio.qutu.destination.mapper.SpotMapper;
import cn.sdstudio.qutu.destination.mapper.SpotTagMapper;
import cn.sdstudio.qutu.destination.mapper.SpotTagMapMapper;
import cn.sdstudio.qutu.destination.service.SpotTagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签Service实现
 *
 * @author qutu
 * @date 2026-02-02
 */
@Service
@RequiredArgsConstructor
public class SpotTagServiceImpl extends ServiceImpl<SpotTagMapper, SpotTag> implements SpotTagService {

    private final SpotTagMapMapper spotTagMapMapper;
    private final SpotMapper spotMapper;

    @Override
    public List<String> getSpotTags(Long spotId) {
        return spotMapper.selectSpotTags(spotId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setSpotTags(Long spotId, List<Long> tagIds) {
        // 删除原有标签关联
        spotTagMapMapper.deleteBySpotId(spotId);
        
        if (tagIds == null || tagIds.isEmpty()) {
            return true;
        }
        
        // 添加新标签关联
        List<SpotTagMap> maps = new ArrayList<>();
        for (Long tagId : tagIds) {
            SpotTagMap map = new SpotTagMap();
            map.setSpotId(spotId);
            map.setTagId(tagId);
            maps.add(map);
        }
        
        for (SpotTagMap map : maps) {
            spotTagMapMapper.insert(map);
        }
        
        return true;
    }

    @Override
    public List<SpotTag> getHotTags(int limit) {
        LambdaQueryWrapper<SpotTag> wrapper = Wrappers.lambdaQuery();
        wrapper.last("LIMIT " + limit);
        return this.list(wrapper);
    }
}
