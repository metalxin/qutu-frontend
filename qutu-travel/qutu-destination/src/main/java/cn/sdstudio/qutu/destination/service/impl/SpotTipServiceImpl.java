package cn.sdstudio.qutu.destination.service.impl;

import cn.sdstudio.qutu.destination.entity.SpotTip;
import cn.sdstudio.qutu.destination.mapper.SpotTipMapper;
import cn.sdstudio.qutu.destination.service.SpotTipService;
import cn.sdstudio.qutu.destination.vo.TipVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 景点提示Service实现
 *
 * @author qutu
 * @date 2026-02-02
 */
@Service
@RequiredArgsConstructor
public class SpotTipServiceImpl extends ServiceImpl<SpotTipMapper, SpotTip> implements SpotTipService {

    @Override
    public List<TipVO> getSpotTips(Long spotId) {
        return baseMapper.selectBySpotId(spotId);
    }
}
